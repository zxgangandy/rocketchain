package com.rocketchain.net.p2p;

import com.rocketchain.net.message.MessageSummarizer;
import com.rocketchain.proto.ProtocolMessage;
import com.rocketchain.utils.exception.StackUtil;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.ImmediateEventExecutor;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.*;
import java.util.stream.Collectors;

public class PeerSet {
    private static final Logger logger = LoggerFactory.getLogger(PeerSet.class);

    private ChannelGroup channelGroup = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
    private Map<InetSocketAddress, Peer> peerByAddress = new HashMap<>();
    private PeerSet thePeerSet;

    public PeerSet create() {
        if (thePeerSet == null) {
            thePeerSet = new PeerSet();
        }

        return thePeerSet;
    }

    /** Add a peer connected via the given channel.
     *
     * @param channel The connected channel.
     * @return
     */
    public Peer add( Channel channel) {
        synchronized(this) {
            SocketAddress remoteAddress = channel.remoteAddress();
            Peer peer = new Peer(channel);
            peerByAddress.put((InetSocketAddress)remoteAddress, peer );
            channelGroup.add(channel);

            logger.trace("Added a peer to channel group :{}", peer);
            return peer;
        }
    }

    public void sendToAll(ProtocolMessage protocolMessage) {
        synchronized(this) {
            String messageString = MessageSummarizer.summarize(protocolMessage);

            if (channelGroup.size() > 0) {
                logger.trace("Sending to all peers : {}", messageString);
            } else {
                logger.warn("No connected peer to send the message : {}", messageString);
            }

            channelGroup.writeAndFlush(protocolMessage).addListener((ChannelGroupFuture future) -> {

                String remoteAddresses = channelGroup.stream()
                        .map(channel -> channel.remoteAddress().toString())
                        .collect(Collectors.joining(","));

                if (future.isSuccess()) {
                    // completed successfully
                    logger.debug("Successfully sent to peers : {}, {}", remoteAddresses, messageString);
                }

                if (future.cause() != null) {
                    // completed with failure

                    String failureDescriptions = "";
                    Iterator<Map.Entry<Channel, Throwable>> iterator = future.cause().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<Channel, Throwable> entry = iterator.next();
                        Channel channel = entry.getKey();
                        Throwable throwable = entry.getValue();

                        if (throwable.getCause() == null) {
                            failureDescriptions += failureDescriptions + "\n";
                        } else {
                            failureDescriptions += "Exception for remote address : " + channel.remoteAddress()
                                    + ", Exception : " + StackUtil.getStackTrace(throwable) + "\n";
                        }
                    }

                    logger.debug("Failed to send to peers : {}, detail : {}", remoteAddresses, failureDescriptions);
                }

                if (future.isCancelled()) {
                    // completed by cancellation
                    logger.debug("Canceled to send to peers : {}, {}", remoteAddresses, messageString);
                }
            });
        }
    }

    public void remove(SocketAddress remoteAddress) {
        synchronized(this) {
            peerByAddress.remove(remoteAddress);
        }
    }

    public List<Peer> all() {
        synchronized(this) {
            return peerByAddress.values().stream()
                    .filter(item -> item.isAlive())
                    .collect(Collectors.toList());
        }
    }

    public synchronized List<Pair<InetSocketAddress, Peer>> peers() {
        synchronized (this) {
            Iterator<Map.Entry<InetSocketAddress, Peer>> iterator = peerByAddress.entrySet().iterator();
            List<Pair<InetSocketAddress, Peer>> list =  new ArrayList<>();

            while (iterator.hasNext()) {
                Map.Entry<InetSocketAddress, Peer> entry = iterator.next();

                InetSocketAddress address = entry.getKey();
                Peer peer = entry.getValue();
                if (peer.isAlive()) {
                    list.add(new MutablePair(address, peer));
                }
            }

//            return peerByAddress.entrySet().stream().map(entry -> {
//                InetSocketAddress address = entry.getKey();
//                Peer peer = entry.getValue();
//                if (peer.isAlive())
//                    return new MutablePair(address, peer);
//                else
//                    return null;
//
//            }).filter(Objects::nonNull).collect(Collectors.toList());

            return list;
        }
    }

}
