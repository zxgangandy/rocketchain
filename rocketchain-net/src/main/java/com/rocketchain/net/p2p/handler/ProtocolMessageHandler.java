package com.rocketchain.net.p2p.handler;

import com.rocketchain.net.p2p.Peer;
import com.rocketchain.proto.ProtocolMessage;

public class ProtocolMessageHandler {
    private Peer peer;

    public ProtocolMessageHandler(Peer peer) {
        this.peer = peer;
    }


    public void handle(ProtocolMessage message) {

    }
}
