package com.rocketchain.net.p2p;

import com.rocketchain.chain.processor.BlockProcessor;
import com.rocketchain.proto.Block;
import com.rocketchain.proto.Hash;

public class BlockPropagator {
    public static void propagate(Hash blockHash , Block block )  {

        // When only one node is running, there is no peer. Need to put the block into the blockchain.
        BlockProcessor.get().acceptBlock(new Hash(blockHash.getValue()), block);
        PeerToPeerNetworking.getPeerCommunicator().propagateBlock(block);
    }
}
