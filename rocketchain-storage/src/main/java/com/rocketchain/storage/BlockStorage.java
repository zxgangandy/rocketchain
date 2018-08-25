package com.rocketchain.storage;

import com.rocketchain.codec.HashUtil;
import com.rocketchain.proto.Block;
import com.rocketchain.proto.BlockHeader;
import com.rocketchain.proto.BlockInfo;
import com.rocketchain.proto.Hash;
import com.rocketchain.storage.index.KeyValueDatabase;
import com.rocketchain.storage.index.TransactionDescriptorIndex;
import com.rocketchain.storage.index.TransactionPoolIndex;
import com.rocketchain.storage.index.TransactionTimeIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface BlockStorage extends BlockDatabase, BlockIndex , TransactionPoolIndex,TransactionTimeIndex, TransactionDescriptorIndex {
    Logger logger = LoggerFactory.getLogger(BlockStorage.class);

     void putBlock(KeyValueDatabase db , Hash blockHash , Block block ) ;

    void close() ;

    /** Get the hash of the next block.
     *
     * @param hash The hash of the block to get the next block of it. The block should exist on the block database.
     * @return Some(hash) if the given block hash is for a block on the best blockchain and not the best block. None otherwise.
     */
    default Hash getNextBlockHash(KeyValueDatabase db , Hash hash){
        // TODO : BUGBUG : Need to add synchronization?
        BlockInfo blockInfo = getBlockInfo(db, hash);
        if (blockInfo != null) {
            return blockInfo.getNextBlockHash();
        } else {
            return null;
        }
    }

    default void putBlock(KeyValueDatabase db , Block block  )  {
        putBlock(db, HashUtil.hashBlockHeader(block.getHeader()), block);
    }

    default void putBlockHeader(KeyValueDatabase db , BlockHeader blockHeader )  {
        putBlockHeader(db, HashUtil.hashBlockHeader(blockHeader), blockHeader);
    }

    default boolean hasBlock(KeyValueDatabase db , Hash blockHash )  {
        BlockInfo blockInfo = getBlockInfo(db, blockHash);
        if (blockInfo != null) {
            return blockInfo.getBlockLocatorOption() != null;
        } else {
            return  false;
        }
    }

    default boolean hasTransaction(KeyValueDatabase db , Hash transactionHash )  {
        // TODO : Optimize : We don't need to deserialize a transaction to see if it exists on our database.
        return getTransaction(db, transactionHash) != null;
    }

    default boolean hasBlockHeader(KeyValueDatabase db , Hash blockHash )  {
        return getBlockHeader(db, blockHash) != null;
    }

    default BlockHeader getBlockHeader(KeyValueDatabase db , Hash blockHash )  {
        // TODO : Check if we need synchronization

        BlockInfo blockInfoOption = getBlockInfo(db, blockHash);
        if (blockInfoOption != null) {
            // case 1 : the block info was found.
            return blockInfoOption.getBlockHeader();
        } else {
            // case 2 : the block info was not found.
            return null;
        }
    }

    default void putBlockHeader(KeyValueDatabase db , Hash blockHash , BlockHeader blockHeader )  {
        // TODO : Check if we need synchronization

        // get the info of the previous block, to calculate the height of the given block and chain-work.
        BlockInfo prevBlockInfoOption = getBlockInfo(db, new Hash(blockHeader.getHashPrevBlock().getValue()));

        // Either the previous block should exist or the block should be the genesis block.
        if (prevBlockInfoOption != null || blockHeader.getHashPrevBlock().isAllZero()) {

            BlockInfo blockInfo = getBlockInfo(db, blockHash);
            if (blockInfo == null) {

                // case 1.1 : the header does not exist yet.
                 blockInfo = BlockInfoFactory.create(
                        // Pass None for the genesis block.
                        prevBlockInfoOption,
                        blockHeader,
                        blockHash,
                        0, // transaction count
                        null // block locator
                );

                putBlockInfo(db, blockHash, blockInfo);
                // We are not checking if the block is the best block, because we received a header only.
                // We put a block as a best block only if we have the block data as long as the header.
            } else {
                // case 1.2 : the same block header already exists.
                logger.trace("A block header is put onto the block database twice. block hash : {}", blockHash);

                // blockIndex hits an assertion if the block header is changed for the same block hash.
                // TODO : Need to change to throw an exception if we try to overwrite with a different block header.
                //blockIndex.putBlockInfo(blockHash, blockInfo.get.copy(
                //  blockHeader = blockHeader
                //))

            }
        } else {
            // case 2 : the previous block header was not found.
            logger.trace("An orphan block was discarded while saving a block header. block header : {}", blockHeader);
        }
    }
}
