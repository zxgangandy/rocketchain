package com.rocketchain.net.p2p.handler;

import com.rocketchain.net.message.VersionFactory;
import com.rocketchain.net.p2p.Peer;
import com.rocketchain.net.p2p.PeerCommunicator;
import com.rocketchain.net.p2p.PeerSet;
import com.rocketchain.proto.ProtocolMessage;
import com.rocketchain.utils.exception.ExceptionUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;

public class NodeServerHandler extends SimpleChannelInboundHandler<ProtocolMessage> {

    private Logger logger = LoggerFactory.getLogger(NodeServerHandler.class);

    private PeerSet peerSet;

    private ProtocolMessageHandler messageHandler;

    public NodeServerHandler(PeerSet peerSet) {
        this.peerSet = peerSet;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        SocketAddress remoteAddress = ctx.channel().remoteAddress();
        logger.info("Connection accepted from {}", remoteAddress);

        assert(messageHandler == null);

        Peer peer = peerSet.add(ctx.channel());
        messageHandler = new ProtocolMessageHandler(peer, new PeerCommunicator(peerSet));

        // Upon successful connection, send the version message.
        peer.send(VersionFactory.create() );

        ctx.channel().closeFuture().addListener(future->{
            peerSet.remove(remoteAddress);

            if (future.isSuccess()) { // completed successfully
                logger.info("Connection closed. Remote address : {}", remoteAddress);
            }

            if (future.cause() != null) { // completed with failure
                String cause = ExceptionUtil.cause(future.cause());
                logger.error("Failed to connect to remote address {}. Exception: {}", remoteAddress, cause);
            }

            if (future.isCancelled()) { // completed by cancellation
                logger.warn("Canceled to close connection. Remote address : {}", remoteAddress);
            }
        });
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ProtocolMessage message) throws Exception {
        assert(messageHandler != null);
        // Process the received message, and send message to peers if necessary.

        logger.info("message: {}", message);
        messageHandler.handle(message);
    }
}
