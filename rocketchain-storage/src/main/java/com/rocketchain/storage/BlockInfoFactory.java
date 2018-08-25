package com.rocketchain.storage;

import com.rocketchain.proto.BlockHeader;
import com.rocketchain.proto.BlockInfo;
import com.rocketchain.proto.FileRecordLocator;
import com.rocketchain.proto.Hash;
import com.rocketchain.utils.lang.HashEstimation;


public class BlockInfoFactory {
    /** Create a block information.
     *
     * @param prevBlockInfoOption The block information of the previous block. Pass None for the genesis block.
     * @param blockHash The hash of the current block.
     * @param blockHeader The block header.
     * @param transactionCount the number of transactions in the block.
     * @return The created block descriptor.
     */
    public static BlockInfo create(BlockInfo prevBlockInfoOption , BlockHeader blockHeader , Hash blockHash ,
                                   int transactionCount , FileRecordLocator blockLocatorOption )  {

        long prevBlockHeight = prevBlockInfoOption == null  ? -1L : prevBlockInfoOption.getHeight();
        long prevBlockChainWork = prevBlockInfoOption == null ? 0L : prevBlockInfoOption.getChainWork();

        return new BlockInfo(
                prevBlockHeight + 1L,
                prevBlockChainWork + HashEstimation.getHashCalculations(blockHash.getValue().getArray()),
                transactionCount,
                null,
                // BUGBUG : Need to use enumeration
                0,
                blockHeader ,
                blockLocatorOption
        );
    }
}
