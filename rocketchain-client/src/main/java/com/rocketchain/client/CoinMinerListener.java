package com.rocketchain.client;

import com.rocketchain.chain.transaction.CoinAddress;
import com.rocketchain.proto.Block;

public interface CoinMinerListener {
    // Called when the coin miner thread starts
    void onStart();
    // Called when the coin is mined.
    void onCoinMined(Block block , CoinAddress minerAddress);
}
