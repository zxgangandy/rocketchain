package com.rocketchain.chain.transaction;

import com.rocketchain.proto.Block;
import com.rocketchain.proto.Hash;

public class MainNetEnv extends NetEnv  {

    public MainNetEnv() {
        CoinbaseMaturity = 100;
    }

    @Override
    public Hash getGenesisBlockHash() {
        return null;
    }

    @Override
    public Block getGenesisBlock() {
        return null;
    }
}
