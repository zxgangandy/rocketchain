package com.rocketchain.net.p2p;

import com.rocketchain.proto.ProtocolMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;


/**
 * Encodes the requested case class that represents a bitcoin protocol message into a {@link ByteBuf}.
 */

@ChannelHandler.Sharable
public class ProtocolEncoder extends MessageToMessageEncoder<ProtocolMessage> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ProtocolMessage protocolMessage, List<Object> list) throws Exception {

    }
}
