package com.rocketchain.net.p2p.handler;

import com.rocketchain.net.p2p.Peer;
import com.rocketchain.net.p2p.PeerCommunicator;
import com.rocketchain.proto.Addr;
import com.rocketchain.proto.ProtocolMessage;
import com.rocketchain.proto.Verack;
import com.rocketchain.proto.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProtocolMessageHandler {
    private Peer peer;
    private PeerCommunicator communicator;

    private Logger logger = LoggerFactory.getLogger(ProtocolMessageHandler.class);

    private MessageHandlerContext context;

    public ProtocolMessageHandler(Peer peer, PeerCommunicator communicator) {
        this.peer = peer;
        this.communicator = communicator;

        this.context = new MessageHandlerContext(peer, communicator);
    }


    public void handle(ProtocolMessage message) {
        if (message instanceof Version) {
            new VersionMessageHandler().handle(context, (Version) message);
        } else if (message instanceof Verack) {
            new VerackMessageHandler().handle(context, (Verack) message);
        } else if (message instanceof Addr) {
            new AddrMessageHandler().handle(context, (Addr) message);
        }
    }
}
