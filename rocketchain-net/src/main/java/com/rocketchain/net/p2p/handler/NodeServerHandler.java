package com.rocketchain.net.p2p.handler;

import com.rocketchain.net.p2p.PeerSet;
import com.rocketchain.proto.ProtocolMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class NodeServerHandler extends SimpleChannelInboundHandler<ProtocolMessage> {
    private PeerSet peerSet;

    public NodeServerHandler(PeerSet peerSet) {
        this.peerSet = peerSet;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ProtocolMessage protocolMessage) throws Exception {

    }
}
