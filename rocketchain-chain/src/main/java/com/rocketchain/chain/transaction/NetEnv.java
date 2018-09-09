package com.rocketchain.chain.transaction;

import com.rocketchain.proto.Block;
import com.rocketchain.proto.Hash;

public abstract class NetEnv {

    public int DefaultBlockVersion = 1;

    /**
     * The version prefix of an address using PubKeyHash.
     */
    protected byte PubkeyAddressVersion = 0;

    /**
     * The version prefix of an address using P2SH.
     */
    protected byte ScriptAddressVersion = 0;

    protected byte SecretKeyVersion ;

    public abstract Hash getGenesisBlockHash();

    public abstract Block getGenesisBlock();

}
