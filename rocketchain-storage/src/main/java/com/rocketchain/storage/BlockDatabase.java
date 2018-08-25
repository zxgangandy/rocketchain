package com.rocketchain.storage;

import com.rocketchain.codec.BlockHeightCodec;
import com.rocketchain.codec.BlockInfoCodec;
import com.rocketchain.codec.HashCodec;
import com.rocketchain.proto.BlockHeight;
import com.rocketchain.proto.BlockInfo;
import com.rocketchain.proto.Hash;
import com.rocketchain.storage.index.KeyValueDatabase;

public interface BlockDatabase {

    default BlockInfo getBlockInfo(KeyValueDatabase db, Hash hash) {
        return db.getObject(new HashCodec(), new BlockInfoCodec(), DB.BLOCK_INFO, hash);
    }

    /**
     * Get the block hash at the given height on the best blockchain.
     *
     * @param height The height of the block.
     * @return The hash of the block at the height on the best blockchain.
     */
    default Hash getBlockHashByHeight(KeyValueDatabase db, long height) {
        return db.getObject(new BlockHeightCodec(), new HashCodec(), DB.BLOCK_HEIGHT, new BlockHeight(height));
    }

    /**
     * Put the block hash searchable by height.
     *
     * @param height The height of the block hash. The block should be on the best blockchain.
     * @param hash   The hash of the block.
     */
    default void putBlockHashByHeight(KeyValueDatabase db, long height, Hash hash) {
        db.putObject(new BlockHeightCodec(), new HashCodec(), DB.BLOCK_HEIGHT, new BlockHeight(height), hash);
    }

    /**
     * Del the block hash by height.
     *
     * @param height the height of the block to delete.
     */
    default void delBlockHashByHeight(KeyValueDatabase db, long height) {
        db.delObject(new BlockHeightCodec(), DB.BLOCK_HEIGHT, new BlockHeight(height));
    }

    /**
     * Update the hash of the next block.
     *
     * @param hash          The block to update the next block hash.
     * @param nextBlockHash Some(nextBlockHash) if the block is on the best blockchain, None otherwise.
     */
    default void updateNextBlockHash(KeyValueDatabase db, Hash hash, Hash nextBlockHash) {
        BlockInfo blockInfoOption = getBlockInfo(db, hash);
        assert (blockInfoOption != null);

        blockInfoOption = new BlockInfo(blockInfoOption.getHeight(),
                blockInfoOption.getChainWork(),
                blockInfoOption.getTransactionCount(),
                nextBlockHash, blockInfoOption.getStatus(),
                blockInfoOption.getBlockHeader(),
                blockInfoOption.getBlockLocatorOption());
        putBlockInfo(db, hash, blockInfoOption);
    }

    default Long getBlockHeight(KeyValueDatabase db, Hash hash) {
        return getBlockInfo(db, hash).getHeight();
    }

    default void putBlockInfo(KeyValueDatabase db, Hash hash, BlockInfo info) {
        BlockInfo blockInfoOption = getBlockInfo(db, hash);
        if (blockInfoOption != null) {
            BlockInfo currentBlockInfo = blockInfoOption;
            // hit an assertion : put a block info with different height
            assert (currentBlockInfo.getHeight() == info.getHeight());

            // hit an assertion : put a block info with a different block locator.
            if (info.getBlockLocatorOption() != null) {
                if (currentBlockInfo.getBlockLocatorOption() != null) {
                    assert (currentBlockInfo.getBlockLocatorOption() == info.getBlockLocatorOption());
                }
            }

            // hit an assertion : change any field on the block header
            assert (currentBlockInfo.getBlockHeader() == info.getBlockHeader());
        }

        db.putObject(new HashCodec(), new BlockInfoCodec(), DB.BLOCK_INFO, hash, info);
    }

    default void putBestBlockHash(KeyValueDatabase db, Hash hash) {
        db.putObject(new HashCodec(), new byte[]{DB.BEST_BLOCK_HASH}, hash);
    }

    default Hash getBestBlockHash(KeyValueDatabase db) {
        return db.getObject(new HashCodec(), new byte[]{DB.BEST_BLOCK_HASH});
    }
}
