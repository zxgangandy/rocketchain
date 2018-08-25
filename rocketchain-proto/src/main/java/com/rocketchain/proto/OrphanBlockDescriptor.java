package com.rocketchain.proto;

public class OrphanBlockDescriptor {
    private Block block;
    public OrphanBlockDescriptor(Block block) {
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }
}
