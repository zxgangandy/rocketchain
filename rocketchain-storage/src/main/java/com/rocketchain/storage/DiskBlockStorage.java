package com.rocketchain.storage;

import com.rocketchain.codec.BlockCodec;
import com.rocketchain.proto.*;
import com.rocketchain.storage.index.BlockDatabaseForRecordStorage;
import com.rocketchain.storage.index.KeyValueDatabase;
import com.rocketchain.storage.record.BlockRecordStorage;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;

public class DiskBlockStorage implements BlockStorage, BlockDatabaseForRecordStorage {
    private KeyValueDatabase db;
    private File directoryPath;
    private int maxFileSize;

    private BlockRecordStorage blockRecordStorage;
    BlockWriter blockWriter;

    private static final int MAX_FILE_SIZE = 1024 * 1024 * 100;
    //val MAX_FILE_SIZE = 1024 * 1024 * 1


    private static DiskBlockStorage theBlockStorage;

    public static BlockStorage create(File storagePath, KeyValueDatabase db) {
        theBlockStorage = new DiskBlockStorage(db, storagePath, MAX_FILE_SIZE);
        return theBlockStorage;
    }

    /**
     * Get the block storage. This actor is a singleton, used by transaction validator.
     *
     * @return The block storage.
     */
    public static BlockStorage get() {
        return theBlockStorage;
    }

    /** Stores block header, block, and transactions in the block.
     *
     * Blocks are stored in two cases.
     *
     * 1. During IBD(Initial block download) process
     *   * We use headers-first approach, so we download all headers from other peers first.
     *     These headers are kept in the key/value database first.
     *   * After all headers are downloaded, we download block data from other peers.
     *     When we store blocks, we store them on the record storage, which writes records on blkNNNNN.dat file.
     *   * After the block data is stored, we update the block info on the key/value database
     *     to point to the record locator on the record storage.
     *
     * 2. After IBD process, we receive a block per (about) 10 minutes
     *    In this case, both header and block data comes together.
     *    We put both block and block header at once.
     *
     *
     * Upon receival of blocks, we maintain the following indexes.
     * keys and values are stored on the key/value database, whereas records are stored on the record storage.
     *
     * 1. (key) block hash -> (value) (block info) record locator -> (record) block data
     * 2. (key) transaction hash -> (value) record locator -> (record) a transaction in the block data.
     * 3. (key) file number -> (value) block file info
     * 4. (key) static -> (value) best block hash
     * 5. (key) static -> (value) last block file number
     *
     * How block headers and blocks are stored :
     *
     * 1. Only blockheader is stored -> A block data is stored. (OK)
     * 2. A block is stored with block header at once. (OK)
     * 3. A block is stored twice. => The second block data is ignored. A warning message is logged.
     * 4. A blockheader is stored twice. => The second header data is ignored. A warning message is logged.
     *
     * @param directoryPath The path where database files are located.
     */
    public DiskBlockStorage(KeyValueDatabase db, File directoryPath, int maxFileSize) {
        this.db = db;
        this.directoryPath = directoryPath;
        this.maxFileSize = maxFileSize;

        //directoryPath.mkdir();
        blockRecordStorage = new BlockRecordStorage(directoryPath, maxFileSize);

        blockWriter = new BlockWriter(blockRecordStorage);
    }

    @Override
    public Pair<BlockInfo, Block> getBlock(KeyValueDatabase db, Hash blockHash) {
        BlockInfo blockInfoOption = getBlockInfo(db, blockHash);
        if (blockInfoOption != null) {
            // case 1 : The block info was found.
            if (blockInfoOption.getBlockLocatorOption() != null) {
                //logger.info(s"getBlock - Found a block info with a locator. block hash : ${blockHash}, locator : ${blockInfoOption.get.blockLocatorOption}")
                // case 1.1 : the block info with a block locator was found.
                return new MutablePair<>(blockInfoOption, blockRecordStorage.readRecord(new BlockCodec(),
                        blockInfoOption.getBlockLocatorOption()));
            } else {
                // case 1.2 : the block info without a block locator was found.
                //logger.info("getBlock - Found a block info without a locator. block hash : {}", blockHash)
                return null;
            }
        } else {
            // case 2 : The block info was not found
            //logger.info("getBlock - No block info found. block hash : {}", blockHash)
            return null;
        }
    }

    @Override
    public Transaction getTransaction(KeyValueDatabase db, Hash transactionHash) {
        return null;
    }

    public void updateFileInfo(FileRecordLocator headerLocator, long fileSize, long blockHeight, long blockTimestamp) {
        FileNumber lastFileNumber = new FileNumber(headerLocator.getFileIndex());

        // Are we writing at the beginning of the file?
        // If yes, we need to update the last block file, because it means we created a file now.
        if (headerLocator.getRecordLocator().getOffset() == 0L) { // case 1 : a new record file was created.
            putLastBlockFile(db, lastFileNumber);
        } else { // case 2 : the block was written on the existing record file.
            // do nothing.
        }

        // Update the block info.
        BlockFileInfo blockFileInfo = getBlockFileInfo(db, lastFileNumber);
        if (blockFileInfo == null) {
            blockFileInfo = new BlockFileInfo(0, 0L, blockHeight, blockHeight, blockTimestamp, blockTimestamp
            );
        }

        // TODO : Need to make sure if it is ok even though a non-best block decreases the lastBlockHeight.
        // Is the lastBlockHeight actually meaning maximum block height?
        blockFileInfo = new BlockFileInfo(blockFileInfo.getBlockCount() + 1,
                fileSize, blockHeight, blockHeight, blockTimestamp, blockTimestamp);
        putBlockFileInfo(db, lastFileNumber, blockFileInfo);
    }


    /**
     * Store a block.
     *
     * @param blockHash the hash of the header of the block to store.
     * @param block     the block to store.
     * @return Boolean true if the block header or block was not existing, and it was put for the first time. false otherwise.
     * submitblock rpc uses this method to check if the block to submit is a new one on the database.
     */
    @Override
    public void putBlock(KeyValueDatabase db, Hash blockHash, Block block) {
        BlockInfo blockInfo = getBlockInfo(db, blockHash);
        boolean isNewBlock = false;

        if (blockInfo != null) {
            // case 1 : block info was found
            if (blockInfo.getBlockLocatorOption() == null) {
                // case 1.1 : block info without a block locator was found
                AppendBlockResult appendResult = blockWriter.appendBlock(block);
                BlockInfo newBlockInfo = new BlockInfo(blockInfo.getHeight(),
                        blockInfo.getChainWork(),
                        block.getTransactions().size(),
                        blockInfo.getNextBlockHash(),
                        blockInfo.getStatus(),
                        blockInfo.getBlockHeader(),
                        appendResult.getBlockLocator());

                putBlockInfo(db, blockHash, newBlockInfo);
                long fileSize = blockRecordStorage.getFiles().get(appendResult.getHeaderLocator().getFileIndex()).size();
                updateFileInfo(appendResult.getHeaderLocator(), fileSize, newBlockInfo.getHeight(), block.getHeader().getTimestamp());
                //logger.info("The block locator was updated. block hash : {}", blockHash)
            } else {
                // case 1.2 block info with a block locator was found
                // The block already exists. Do not put it once more.
                logger.trace("The block already exists. block hash : {}", blockHash);
            }
        } else {
            // case 2 : no block info was found.
            // get the info of the previous block, to calculate the height and chain-work of the given block.
            BlockInfo prevBlockInfoOption = getBlockInfo(db, new Hash(block.getHeader().getHashPrevBlock().getValue()));

            // Either the previous block should exist or the block should be the genesis block.
            if (prevBlockInfoOption != null || block.getHeader().getHashPrevBlock().isAllZero()) {
                // case 2.1 : no block info was found, previous block header exists.
                AppendBlockResult appendResult = blockWriter.appendBlock(block);

                blockInfo = BlockInfoFactory.create(
                        // For the genesis block, the prevBlockInfoOption is None.
                        prevBlockInfoOption,
                        block.getHeader(),
                        blockHash,
                        block.getTransactions().size(), // transaction count
                        appendResult.getBlockLocator() // block locator
                );

                putBlockInfo(db, blockHash, blockInfo);

                long blockHeight = blockInfo.getHeight();
                long fileSize = blockRecordStorage.getFiles().get(appendResult.getHeaderLocator().getFileIndex()).size();
                updateFileInfo(appendResult.getHeaderLocator(), fileSize, blockInfo.getHeight(), block.getHeader().getTimestamp());

                isNewBlock = true;
                //logger.info("The block was put. block hash : {}", blockHash)
            } else {
                // case 2.2 : no block info was found, previous block header does not exists.
                // Actually the code execution should never come to here, because we have checked if the block is an orphan block
                // before invoking putBlock method.
                logger.trace("An orphan block was discarded while saving a block. block hash : {}", block.getHeader());
            }
        }
    }

    @Override
    public void close() {
        blockRecordStorage.close();
    }
}
