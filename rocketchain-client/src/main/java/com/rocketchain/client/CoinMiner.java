package com.rocketchain.client;

import com.rocketchain.chain.Blockchain;
import com.rocketchain.net.p2p.PeerCommunicator;
import com.rocketchain.storage.index.KeyValueDatabase;

public class CoinMiner {
    private KeyValueDatabase db ;
    private String minerAccount ;
    private Blockchain chain ;
    private PeerCommunicator peerCommunicator;
    private CoinMinerParams params;
    private CoinMinerListener listener;
}
