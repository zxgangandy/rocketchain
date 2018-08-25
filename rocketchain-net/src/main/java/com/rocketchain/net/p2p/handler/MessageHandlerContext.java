package com.rocketchain.net.p2p.handler;

import com.rocketchain.net.p2p.Peer;
import com.rocketchain.net.p2p.PeerCommunicator;


/**
 * The context for handling messages for a peer.
 * In case state transitions are required for handling messages for a peer, the states are kept in the message handler context of it.
 *
 */
public class MessageHandlerContext {
    private Peer peer;
    private PeerCommunicator communicator;

    /**
     * @param peer The peer that this node is handler is communicating.
     * @param communicator The peer communicator that can communicate with any of peers connected to this node.
     */
    public MessageHandlerContext(Peer peer, PeerCommunicator communicator) {
        this.peer = peer;
        this.communicator = communicator;
    }

    public Peer getPeer() {
        return peer;
    }

    public PeerCommunicator getCommunicator() {
        return communicator;
    }
}
