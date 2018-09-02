package com.rocketchain.chain.processor;

import com.rocketchain.chain.Blockchain;
import com.rocketchain.proto.Block;
import com.rocketchain.proto.Hash;
import com.rocketchain.storage.index.KeyValueDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockProcessor {
    private Logger logger = LoggerFactory.getLogger(BlockProcessor.class);

    private KeyValueDatabase db;
    private Blockchain chain;

    public BlockProcessor(KeyValueDatabase db, Blockchain chain) {
        this.db = db;
        this.chain = chain;
    }


    /**
     * Validate a block.
     *
     * @param block
     * @return
     */
    public void validateBlock(Block block) {
        // Step 1. check the serialized block size.
        // Step 2. check the proof of work - block hash vs target hash
        // Step 3. check the block timestamp.
        // Step 4. check the first transaction is coinbase, and others are not.
        // Step 5. check each transaction in a block.
        // Step 6. check the number of script operations on the locking/unlocking script.
        // Step 7. Calculate the merkle root hash, compare it with the one in the block header.
        // TODO : Implement
        // Step 8. Make sure that the same hash with the genesis transaction does not exist. If exists, throw an error saying that the coinbase data needs to have random data to make generation transaction id different from already existing ones.
        //    assert(false)
/*
    val message = s"The block is invalid(${outPoint})."
    logger.warn(message)
    throw ChainException(ErrorCode.InvalidBlock, message)
*/
    }

    /**
     * Put the block into the blockchain. If a fork becomes the new best blockchain, do block reorganization.
     *
     * @param blockHash The hash of the block to accept.
     * @param block     The block to accept.
     * @return true if the newly accepted block became the new best block.
     */
    public boolean acceptBlock(Hash blockHash, Block block) {
        // Step 1. Need to check if the same blockheader hash exists by looking up mapBlockIndex
        // Step 2. Need to increase DoS score if an orphan block was received.
        // Step 3. Need to increase DoS score if the block hash does not meet the required difficulty.
        // Step 4. Need to get the median timestamp for the past N blocks.
        // Step 5. Need to check the lock time of all transactions.
        // Step 6. Need to check block hashes for checkpoint blocks.
        // Step 7. Write the block on the block database, reorganize blocks if necessary.
        //chain.withTransaction { implicit transactingDB =>
        //  chain.putBlock(blockHash, block)(transactingDB)
        //}

        // TODO : BUGBUG : Change to record level locking with atomic update.
        return chain.putBlock(db, blockHash, block);
    }

    /** Get a block.
     *
     * @param blockHash The hash of the block to get.
     * @return Some(block) if the block exists; None otherwise.
     */
    public Block getBlock(Hash blockHash )  {
        return chain.getBlock(db, blockHash).getValue();
    }

    private static BlockProcessor theBlockProcessor;

    public static BlockProcessor create(Blockchain chain) {
        if (theBlockProcessor == null) {
            theBlockProcessor = new BlockProcessor(chain.getDb(), chain);
        }
        return theBlockProcessor;
    }

    public static BlockProcessor get() {
        return theBlockProcessor;
    }
}
