package com.rocketchain.net.p2p;

import com.rocketchain.codec.BitcoinProtocol;
import com.rocketchain.codec.BitcoinProtocolCodec;
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
public class BitcoinProtocolEncoder extends MessageToMessageEncoder<ProtocolMessage> {
    private BitcoinProtocolCodec codec = new BitcoinProtocolCodec( new BitcoinProtocol() );

    /**
     * Allocate a {@link ByteBuf} which will be used for constructing an encoded byte buffer of protocol message.
     * BUGBUG : Modify this method to return a {@link ByteBuf} with a perfect matching initial capacity.
     */
    private ByteBuf allocateBuffer(
            ChannelHandlerContext ctx,
            @SuppressWarnings("unused") ProtocolMessage msg) throws Exception {
        return ctx.alloc().ioBuffer(1024);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ProtocolMessage msg, List<Object> out) throws Exception {
        ByteBuf encodedByteBuf = allocateBuffer(ctx, msg);

        codec.encode(msg, encodedByteBuf);

        out.add(encodedByteBuf);
    }
}
