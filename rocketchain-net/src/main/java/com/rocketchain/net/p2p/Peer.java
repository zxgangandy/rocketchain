package com.rocketchain.net.p2p;

import com.rocketchain.net.message.MessageSummarizer;
import com.rocketchain.net.p2p.client.NodeClient;
import com.rocketchain.proto.ProtocolMessage;
import com.rocketchain.proto.Version;
import io.netty.channel.Channel;
import com.rocketchain.utils.exception.ExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a connected peer.
 */
public class Peer {
    private final Logger logger = LoggerFactory.getLogger(NodeClient.class);

    /**
     * The version we got from the peer. This is set to some value only if we received the Version message.
     */
    private Version versionOption;

    private Channel channel;

    public Peer(Channel channel) {
        this.channel = channel;
    }

    public Channel getChannel() {
        return channel;
    }

    public boolean isAlive() {
        return channel.isOpen() && channel.isActive();
    }


    public  void send(ProtocolMessage message) {
        String messageString = MessageSummarizer.summarize(message);

        channel.writeAndFlush(message).addListener(channelFuture -> {
            if (channelFuture.isSuccess()) {
                logger.info("Successfully send to peer {}, message {}", channel.remoteAddress(), messageString);
                return;
            }
            if (channelFuture.cause() != null) {
                String cause = ExceptionUtil.cause(channelFuture.cause());

                logger.error("Failed to send to peer {}, message {}. Exception: {}", channel.remoteAddress(), messageString, cause);
                return;
            }
            if (channelFuture.isCancelled()) {
                logger.warn("Connecting to peer {}, message {} has been cancelled", channel.remoteAddress(), messageString);
            }
        });

    }

    /**
     * Update version received from the peer.
     *
     * @param version The version received from the peer.
     */
    public void updateVersion(Version version)  {
        versionOption = version;
    }

    public Version getVersionOption() {
        return versionOption;
    }
}
