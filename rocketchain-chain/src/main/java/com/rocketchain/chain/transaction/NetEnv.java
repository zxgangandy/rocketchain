package com.rocketchain.chain.transaction;

import com.rocketchain.proto.Block;
import com.rocketchain.proto.Hash;

public abstract class NetEnv {

    public int DefaultBlockVersion = 1;

    public abstract Hash getGenesisBlockHash();

    public abstract Block getGenesisBlock();
}
