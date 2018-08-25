package com.rocketchain.net.p2p.handler;

import com.rocketchain.net.p2p.Peer;
import com.rocketchain.net.p2p.PeerSet;
import com.rocketchain.proto.ProtocolMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import com.rocketchain.utils.exception.ExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NodeClientHandler extends SimpleChannelInboundHandler<ProtocolMessage> {

    private final Logger logger = LoggerFactory.getLogger(NodeClientHandler.class);

    private ProtocolMessageHandler protocolMessageHandler;
    private PeerSet peerSet;

    public NodeClientHandler(PeerSet peerSet) {
        this.peerSet = peerSet;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ProtocolMessage protocolMessage) throws Exception {
        if (protocolMessageHandler == null) {
            Peer peer = peerSet.add(channelHandlerContext.channel());
            protocolMessageHandler = new ProtocolMessageHandler(peer);
        }

        // Process the received message, and send message to peers if necessary.
        protocolMessageHandler.handle(protocolMessage);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        String causeException = ExceptionUtil.cause(cause);
        logger.error("Exception: {}", causeException);

        ctx.close();
    }
}
