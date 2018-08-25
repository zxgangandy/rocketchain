package com.rocketchain.net.p2p.server;

import com.rocketchain.net.p2p.PeerSet;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NodeServer {
    private static final Logger logger = LoggerFactory.getLogger(NodeServer.class);

    private  EventLoopGroup bossGroup   = new NioEventLoopGroup(1);
    private  EventLoopGroup workerGroup = new NioEventLoopGroup();

    private PeerSet peerSet;

    public NodeServer(PeerSet peerSet) {
        this.peerSet = peerSet;
    }

    public ChannelFuture listen(int port) {
        ServerBootstrap b = new ServerBootstrap();

        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new NodeServerInitializer(peerSet));

        return b.bind(port).addListener(future-> {
                if (future.isSuccess()) { // completed successfully
                    logger.info("Successfully bound port : {}", port);
                }

                if (future.cause() != null) { // completed with failure
                    logger.error("Failed to bind port : {}. Exception : {}",
                            port, future.cause().getMessage());
                }

                if (future.isCancelled()) { // completed by cancellation
                    logger.error("Canceled to bind port : {}", port);
                }

        });
    }

    public void shutdown(){
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
