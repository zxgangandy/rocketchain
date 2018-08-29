package com.rocketchain.net.p2p;

import com.rocketchain.net.message.VersionFactory;
import com.rocketchain.net.p2p.client.NodeClient;
import com.rocketchain.utils.exception.ExceptionUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

public class RetryingConnector {
    private final Logger logger = LoggerFactory.getLogger(RetryingConnector.class);

    private PeerSet peerSet;
    private int retryIntervalSeconds;

    public RetryingConnector(PeerSet peerSet, int retryIntervalSeconds) {
        this.peerSet = peerSet;
        this.retryIntervalSeconds = retryIntervalSeconds;
    }

    public void connect(String address, int port) {
        NodeClient nodeClient = new NodeClient(peerSet);
        ChannelFuture channelFuture = nodeClient.connect(address, port);

        channelFuture.addListener((ChannelFuture future) -> {
            Channel channel = future.channel();
            if (channelFuture.isSuccess()) {
                logger.info("Sending version message to {}", channel.remoteAddress());

                // Upon successful connection, send the version message.
                channel.writeAndFlush(VersionFactory.create());


                future.channel().closeFuture().addListener((ChannelFuture cf) -> {

                    assert (cf.isDone());

                    if (future.isSuccess()) { // completed successfully
                        logger.info("Connection closed. Remote address : {}", channel.remoteAddress());
                    }

                    if (future.cause() != null) { // completed with failure
                        String cause = ExceptionUtil.cause(cf.cause());
                        logger.error("Failed to connect to remote address {}. Exception: {}", channel.remoteAddress(), cause);
                    }

                    if (cf.isCancelled()) { // completed by cancellation
                        logger.warn("Canceled to close connection. Remote address : {}", channel.remoteAddress());
                    }

                    peerSet.remove(channel.remoteAddress());
                });
            } else {
                channel.close();
                nodeClient.close();

                // TODO : Do we need to check future.isCanceled()?
                logger.info("Connection to {}:{} failed. Will try in a second.", address, port);
//                Timer timer = new Timer(true);
//                timer.schedule(new TimerTask() {
//                    @Override
//                    public void run() {
//                        connect(address, port);
//                    }
//                }, retryIntervalSeconds * 1000);
            }
        });
    }
}
