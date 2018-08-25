package com.rocketchain.chain.transaction;

import com.rocketchain.proto.Block;

public class ChainBlock {
    private long height;
    private Block block;

    public ChainBlock(long height, Block block) {
        this.height = height;
        this.block = block;
    }

    public long getHeight() {
        return height;
    }

    public Block getBlock() {
        return block;
    }
}
