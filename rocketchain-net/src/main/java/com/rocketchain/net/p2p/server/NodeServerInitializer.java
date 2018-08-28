package com.rocketchain.net.p2p.server;

import com.rocketchain.net.p2p.BitcoinProtocolDecoder;
import com.rocketchain.net.p2p.BitcoinProtocolEncoder;
import com.rocketchain.net.p2p.PeerSet;
import com.rocketchain.net.p2p.handler.NodeServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class NodeServerInitializer extends ChannelInitializer<SocketChannel> {
    private PeerSet peerSet;

    public NodeServerInitializer(PeerSet peerSet) {
        this.peerSet = peerSet;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();

        // On top of the SSL handler, add the text line codec.
        pipeline.addLast(new BitcoinProtocolDecoder());
        pipeline.addLast(new BitcoinProtocolEncoder());
        // and then business logic.
        pipeline.addLast(new NodeServerHandler(peerSet));
    }
}
