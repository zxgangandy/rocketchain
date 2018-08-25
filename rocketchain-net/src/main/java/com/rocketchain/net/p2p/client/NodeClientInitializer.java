package com.rocketchain.net.p2p.client;

import com.rocketchain.net.p2p.PeerSet;
import com.rocketchain.net.p2p.handler.NodeClientHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NodeClientInitializer extends ChannelInitializer<NioSocketChannel> {
    private String ip;
    private int port;
    private PeerSet peerSet;

    public NodeClientInitializer(String ip, int port, PeerSet peerSet) {
        this.ip = ip;
        this.port = port;
        this.peerSet = peerSet;
    }

    @Override
    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
        ChannelPipeline pipeline = nioSocketChannel.pipeline();


        // and then business logic.
        pipeline.addLast(new NodeClientHandler(peerSet));
    }
}
