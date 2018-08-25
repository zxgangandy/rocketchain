package com.rocketchain.chain;

import com.rocketchain.proto.Hash;

import java.util.List;

public class BlockLocatorHashes {
    private List<Hash> hashes;

    public BlockLocatorHashes(List<Hash> hashes) {
        this.hashes = hashes;
    }

    public List<Hash> getHashes() {
        return hashes;
    }
}
