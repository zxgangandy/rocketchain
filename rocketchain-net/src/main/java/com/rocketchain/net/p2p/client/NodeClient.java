package com.rocketchain.net.p2p.client;

import com.rocketchain.net.p2p.PeerSet;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultMessageSizeEstimator;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import com.rocketchain.utils.exception.ExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NodeClient implements AutoCloseable {

    private final Logger logger = LoggerFactory.getLogger(NodeClient.class);

    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    private PeerSet peerSet;

    public NodeClient(PeerSet peerSet) {
        this.peerSet = peerSet;
    }

    public ChannelFuture connect(String address, int port) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup);

        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.option(ChannelOption.MESSAGE_SIZE_ESTIMATOR, DefaultMessageSizeEstimator.DEFAULT);

        bootstrap.handler(new NodeClientInitializer(address, port, peerSet));

        logger.info("Connecting to ip {}, port {}", address, port);
        return bootstrap.connect(address, port).addListener(channelFuture -> {
            if (channelFuture.isSuccess()) {
                logger.info("Successfully connect to ip {}, port {}", address, port);
                return;
            }
            if (channelFuture.cause() != null) {
                String cause = ExceptionUtil.cause(channelFuture.cause());

                logger.error("Failed to connect to address {}, port {}. Exception: {}", address, port, cause);
                return;
            }
            if (channelFuture.isCancelled()) {
                logger.warn("Cancelled to connect to address {}, port {} has been cancelled", address, port);
            }
        });
    }


    @Override
    public void close() throws Exception {
        workerGroup.shutdownGracefully();
    }
}
