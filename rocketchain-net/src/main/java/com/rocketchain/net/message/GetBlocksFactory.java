package com.rocketchain.net.message;

import com.rocketchain.chain.BlockLocator;
import com.rocketchain.chain.Blockchain;
import com.rocketchain.chain.transaction.MainNetEnv;
import com.rocketchain.chain.transaction.NetEnv;
import com.rocketchain.chain.transaction.NetEnvFactory;
import com.rocketchain.proto.GetBlocks;
import com.rocketchain.proto.Hash;

import java.util.List;

public class GetBlocksFactory {
    /** Create a GetBlocks message to get the given block.
     *
     * @param blockHashToGet The hash of the block to get.
     * @return
     */
    public static GetBlocks create(Hash blockHashToGet)  {
        NetEnv env = NetEnvFactory.get();

        BlockLocator locator = new BlockLocator(Blockchain.get());
        List<Hash> blockLocatorHashes = locator.getLocatorHashes().getHashes();
        return new GetBlocks(env.DefaultBlockVersion, blockLocatorHashes, blockHashToGet);
    }
}
