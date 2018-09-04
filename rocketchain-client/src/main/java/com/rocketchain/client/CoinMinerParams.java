package com.rocketchain.client;

public class CoinMinerParams {

    private int P2PPort;
    private int MaxBlockSize;

    public CoinMinerParams(int p2PPort, int maxBlockSize) {
        P2PPort = p2PPort;
        MaxBlockSize = maxBlockSize;
    }

    public int getP2PPort() {
        return P2PPort;
    }

    public int getMaxBlockSize() {
        return MaxBlockSize;
    }
}
