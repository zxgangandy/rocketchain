package com.rocketchain.net.p2p;

import com.rocketchain.net.p2p.server.NodeServer;
import com.rocketchain.utils.net.PeerAddress;
import io.netty.channel.ChannelFuture;

import java.util.List;

public class PeerToPeerNetworking {
    private static PeerCommunicator peerCommunicator;

    public static PeerCommunicator createPeerCommunicator(int inboundPort, List<PeerAddress> peerAddresses) {
        PeerSet peerSet = new PeerSet().create();

        // TODO : BUGBUG : Need to call nodeServer.shutdown before the process finishes ?
        NodeServer nodeServer = new NodeServer(peerSet);
        ChannelFuture bindChannelFuture  = nodeServer.listen(inboundPort);
        // Wait until the inbound port is bound.
        try {
            bindChannelFuture.sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        peerAddresses.stream().forEach(peer->{
            new RetryingConnector(peerSet, 1).connect(peer.getAddress(), peer.getPort());
        });

        peerCommunicator = new PeerCommunicator(peerSet);
        return peerCommunicator;
    }


    public static PeerCommunicator getPeerCommunicator() {
        return peerCommunicator;
    }
}
