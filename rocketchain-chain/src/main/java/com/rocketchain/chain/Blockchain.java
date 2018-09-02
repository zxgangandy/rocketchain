package com.rocketchain.chain;

import com.rocketchain.chain.transaction.BlockchainView;
import com.rocketchain.chain.transaction.ChainBlock;
import com.rocketchain.codec.HashUtil;
import com.rocketchain.proto.Block;
import com.rocketchain.proto.BlockInfo;
import com.rocketchain.proto.Hash;
import com.rocketchain.proto.Transaction;
import com.rocketchain.storage.BlockStorage;
import com.rocketchain.storage.index.KeyValueDatabase;
import com.rocketchain.utils.exception.ChainException;
import com.rocketchain.utils.exception.ErrorCode;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

/**
 * Maintains the best blockchain, whose chain work is the biggest one.
 * <p>
 * The block metadata is kept in a tree data structure on-disk.
 * The actual block data is also kept on-disk.
 * <p>
 * < Overview >
 * <p>
 * The chain work for a block is the total number of hash calculations from block 0 to the current best block.
 * <p>
 * For example, if we calculated hashes 10, 20, 15 times for three blocks B0, B1, and B2, the chain work is 45(10+20+15).
 * <p>
 * B0(10) → B1(10+20) → B2(10+20+15) : The best chain.
 * <p>
 * Based on the total chain work of the new block, we decide the best blockchain.
 * For example, if we found a block B2' whose chain work(50) is greater than the current maxium(45),
 * we will keep B2' as the best block and update the best blockchain.
 * <p>
 * B0(10) → B1(10+20) → B2'(10+20+20) : The best chain.
 * ↘ B2(10+20+15) : This is a fork.
 * <p>
 * When a block B3 is added to the blockchain, we will add it on top of the best blockchain.
 * <p>
 * B0 → B1 → B2' → B3 : The best chain.
 * ↘ B2
 * <p>
 * Only transactions in the best blockchain remain effective.
 * Because B2 remains in a fork, all transactions in B2 are migrated to the disk-pool, except ones that are included in B3.
 * <p>
 * The disk-pool is where transactions that are not in any block of the best blockchain are stored.
 * ( Bitcoin core stores transactions in memory using mempool, but RocketChain stores transactions on-disk using disk-pool ;-). )
 * TransactionDescriptor can either store record location of the transaction if the transaction was written as part of a block on disk.
 * Otherwise, TransactionDescriptor can stores a serialized transaction, and TransactionDescriptor itself is stored as a value of RocksDB keyed by the transaction hash.
 * <p>
 * Of course, block a reorganization can invalidate more than two blocks at once.
 * <p>
 * Time T :
 * B0(10) → B1(30) → B2(45) : The best chain.
 * <p>
 * Time T+1 : Add B1' (chain work = 35)
 * B0(10) → B1(30) → B2(45) : The best chain.
 * ↘ B1'(35)
 * <p>
 * Time T+2 : Add B2' (chain work = 55)
 * B0(10) → B1(30) → B2(45)
 * ↘ B1'(35) -> B2'(55) : The best chain.
 * <p>
 * In this case all transactions in B1, B2 but not in B1' and B2' are moved to the disk-pool so that they can be added to
 * the block chain later when a block is created.
 */
public class Blockchain implements BlockchainView {
    private Logger logger = LoggerFactory.getLogger(Blockchain.class);

    private KeyValueDatabase db;
    private BlockStorage storage;

    private BlockInfo theBestBlock = null;

    private TransactionMagnet txMagnet;
    private BlockMagnet blockMagnet;
    private TransactionPool txPool;
    private TransactionOrphanage txOrphanage;

    public Blockchain(KeyValueDatabase db, BlockStorage storage) {
        this.db = db;
        this.storage = storage;

        this.txPool = new TransactionPool(storage, txMagnet);
        this.txMagnet = new TransactionMagnet(storage, storage, storage);
        this.blockMagnet = new BlockMagnet(storage, txPool, txMagnet);
        this.txOrphanage = new TransactionOrphanage(storage);
    }

    private static Blockchain theBlockchain = null;

    public static Blockchain create(KeyValueDatabase db, BlockStorage storage) {
        theBlockchain = new Blockchain(db, storage);

        // Load any in memory structur required by the Blockchain class from the on-disk storage.
        new BlockchainLoader(db, theBlockchain, storage).load();
        return theBlockchain;
    }

    public TransactionOrphanage getTxOrphanage() {
        return txOrphanage;
    }

    /**
     * Check if the transaction exists either in a block on the best blockchain or on the transaction pool.
     *
     * @param txHash The hash of the transaction to check the existence.
     * @return true if we have the transaction; false otherwise.
     */
    public boolean hasTransaction(KeyValueDatabase db, Hash txHash) {
        return storage.getTransactionDescriptor(db, txHash) != null || storage.getTransactionFromPool(db, txHash) != null;
    }

    /**
     * Return the hash of block on the tip of the best blockchain.
     *
     * @return The best block hash.
     */
    public Hash getBestBlockHash(KeyValueDatabase db) {
        return storage.getBestBlockHash(db);
    }

    public static Blockchain get() {
        return theBlockchain;
    }

    /**
     * Put a transaction we received from peers into the disk-pool.
     *
     * @param transaction The transaction to put into the disk-pool.
     */
    public void putTransaction(KeyValueDatabase db, Hash txHash, Transaction transaction) {
        // TODO : BUGBUG : Need to start a RocksDB transaction.
        try {
            // Step 1 : Add transaction to the transaction pool.
            txPool.addTransactionToPool(db, txHash, transaction);

            // TODO : BUGBUG : Need to commit the RocksDB transaction.

        } finally {
            // TODO : BUGBUG : Need to rollback the RocksDB transaction if any exception raised.
            // Only some of inputs might be connected. We need to revert the connection if any error happens.
        }
    }

    /**
     * Put a block onto the blockchain.
     * <p>
     * (1) During initialization, we call putBlock for each block we received until now.
     * (2) During IBD(Initial Block Download), we call putBlock for blocks we downloaded.
     * (3) When a new block was received from a peer.
     * (4) A new block was submitted by SubmitBlock
     * (5) A block was mined by the miner thread.
     * <p>
     * Caller of this method should check if the bestBlock was changed.
     * If changed, we need to update the best block on the storage layer.
     * <p>
     * TODO : Need to check the merkle root hash in the block.
     *
     * @param block The block to put into the blockchain.
     * @return true if the newly accepted block became the new best block.
     */
    public boolean putBlock(KeyValueDatabase db, Hash blockHash, Block block) {

        // TODO : BUGBUG : Need to think about RocksDB transactions.

        synchronized (this) {
            if (storage.hasBlock(db, blockHash)) {
                logger.trace("Duplicate block was ignored. Block hash : ${blockHash}");
                return false;
            } else {

                // Case 1. If it is the genesis block, set the genesis block as the current best block.
                if (block.getHeader().getHashPrevBlock().isAllZero()) {
                    assert (theBestBlock == null);

                    storage.putBlock(db, block);

                    BlockInfo blockInfo = storage.getBlockInfo(db, blockHash);

                    // Attach the block. ChainEventListener is invoked in this method.
                    // TODO : BUGBUG : Before attaching a block, we need to test if all transactions in the block can be attached.
                    // - If any of them are not attachable, the blockchain remains in an inconsistent state because only part of transactions are attached.
                    blockMagnet.attachBlock(db, blockInfo, block);

                    setBestBlock(db, blockHash, blockInfo);

                    return true;
                } else { // Case 2. Not a genesis block.
                    assert (theBestBlock != null);

                    // Step 2.1 : Get the block descriptor of the previous block.
                    // We already checked if the parent block exists so it is safe to call with '!!'
                    BlockInfo prevBlockDesc = storage.getBlockInfo(db, block.getHeader().getHashPrevBlock());

                    Hash prevBlockHash = HashUtil.hashBlockHeader(prevBlockDesc.getBlockHeader());

                    storage.putBlock(db, block);
                    BlockInfo blockInfo = storage.getBlockInfo(db, blockHash);

                    // Case 2.A : The previous block of the block is the current best block.
                    if (prevBlockHash == HashUtil.hashBlockHeader(theBestBlock.getBlockHeader())) {
                        // Step 2.A.1 : Attach the block. ChainEventListener is invoked in this method.
                        blockMagnet.attachBlock(db, blockInfo, block);

                        // Step 2.A.2 : Update the best block
                        setBestBlock(db, blockHash, blockInfo);

                        // TODO : Update best block in wallet (so we can detect restored wallets)
                        //assert(getBestBlockHash(db) == blockHash)
                        logger.info("Successfully have put the block in the best blockchain.\n Height : ${blockInfo.height}, Hash : ${blockHash}");
                        return true;
                    } else { // Case 2.B : The previous block of the new block is NOT the current best block.
                        // Step 3.B.1 : See if the chain work of the new block is greater than the best one.
                        if (blockInfo.getChainWork() > theBestBlock.getChainWork()) {
                            logger.info("Block reorganization started. Original Best : (${theBestBlock!!.blockHeader.hash()}," +
                                    "${theBestBlock}), The Best (${blockInfo.blockHeader.hash()},${blockInfo})");

                            // Step 3.B.2 : Reorganize the blocks.
                            // transaction handling, orphan block handling is done in this method.
                            blockMagnet.reorganize(db, theBestBlock, blockInfo);


                            // Step 3.B.3 : Update the best block
                            setBestBlock(db, blockHash, blockInfo);

                            // TODO : Update best block in wallet (so we can detect restored wallets)
                            return true;
                        } else {
                            logger.info("A block was added to a fork. The current Best : (${theBestBlock!!.blockHeader.hash()}," +
                                    "${theBestBlock}), The best on the fork : (${blockInfo.blockHeader.hash()},${blockInfo})");
                            return false;
                        }
                    }
                }
            }
        }
    }


    /**
     * Put the best block hash into on-disk storage, as well as the in-memory best block info.
     *
     * @param blockHash
     * @param blockInfo
     */
    public void setBestBlock(KeyValueDatabase db, Hash blockHash, BlockInfo blockInfo) {
        theBestBlock = blockInfo;
        storage.putBestBlockHash(db, blockHash);
    }

//
//    public  <T> T withTransaction( (KeyValueDatabase-> T) block )  {
//        TransactingKeyValueDatabase transactingRocksDB = db.transacting();
//
//        transactingRocksDB.beginTransaction();
//
//        val returnValue =
//        try {
//            block(transactingRocksDB);
//        } catch ( Throwable t  ) {
//            transactingRocksDB.abortTransaction();
//            throw t;
//        }
//
//        transactingRocksDB.commitTransaction();
//        return returnValue;
//    }


    /**
     * Get the hash of a block specified by the block height on the best blockchain.
     * <p>
     * Used by : getblockhash RPC.
     *
     * @param blockHeight The height of the block.
     * @return The hash of the block header.
     */
    public Hash getBlockHash(KeyValueDatabase db, long blockHeight) {
        Hash blockHashOption = storage.getBlockHashByHeight(db, blockHeight);
        // TODO : Bitcoin Compatiblity : Make the error code compatible when the block height was a wrong value.
        if (blockHashOption == null) {
            throw new ChainException(ErrorCode.InvalidBlockHeight);
        }
        return blockHashOption;
    }


    /**
     * Get a block searching by the header hash.
     * <p>
     * Used by : getblock RPC.
     *
     * @param blockHash The header hash of the block to search.
     * @return The searched block.
     */
    public Pair<BlockInfo, Block> getBlock(KeyValueDatabase db, Hash blockHash) {
        return storage.getBlock(db, blockHash);
    }


    /**
     * Return an iterator that iterates each ChainBlock from a given height.
     * <p>
     * Used by : importAddress RPC to rescan the blockchain.
     *
     * @param fromHeight Specifies where we start the iteration. The height 0 means the genesis block.
     * @return The iterator that iterates each ChainBlock.
     */
    @Override
    public Iterator<ChainBlock> getIterator(KeyValueDatabase db, long fromHeight) {
//        Hash bestBlockHash = storage.getBestBlockHash(db);
//        long bestBlockHeight = storage.getBlockHeight(db, bestBlockHash);
//
//        val chainBlockSequence = (fromHeight..bestBlockHeight).asSequence().map {
//            height ->
//                    val blockHash = storage.getBlockHashByHeight(db, height) ;
//                    val(unused, block) = storage.getBlock(db, blockHash) ;
//                    ChainBlock(height, block);
//        }
//        return chainBlockSequence.iterator();
        return null;
    }

    /**
     * Return the block height of the best block.
     *
     * @return The best block height.
     */
    @Override
    public long getBestBlockHeight() {
        assert (theBestBlock != null);
        return theBestBlock.getHeight();
    }

    @Override
    public Transaction getTransaction(KeyValueDatabase db, Hash transactionHash) {
        // Note : No need to search transaction pool, as storage.getTransaction searches the transaction pool as well.

        // Step 1 : Search block database.
        Transaction dbTransactionOption = storage.getTransaction(db, transactionHash);

        // Step 3 : TODO : Run validation.

        //BUGBUG : Transaction validation fails because the transaction hash on the outpoint does not exist.
        //poolTransactionOption.foreach( TransactionVerifier(_).verify(DiskBlockStorage.get) )
        //dbTransactionOption.foreach( TransactionVerifier(_).verify(DiskBlockStorage.get) )

        return dbTransactionOption;
    }

    /**
     * See if a block exists on the blockchain.
     * <p>
     * Used by : submitblock RPC to check if a block already exists.
     *
     * @param blockHash The hash of the block header to check.
     * @return true if the block exists; false otherwise.
     */
    public boolean hasBlock(KeyValueDatabase db, Hash blockHash) {
        return storage.hasBlock(db, blockHash);
    }

    public BlockInfo getTheBestBlock() {
        return theBestBlock;
    }

    public void setTheBestBlock(BlockInfo theBestBlock) {
        this.theBestBlock = theBestBlock;
    }

    public KeyValueDatabase getDb() {
        return db;
    }
}
