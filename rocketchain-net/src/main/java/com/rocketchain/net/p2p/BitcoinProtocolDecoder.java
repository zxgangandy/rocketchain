package com.rocketchain.net.p2p;

import com.rocketchain.codec.BitcoinProtocol;
import com.rocketchain.codec.BitcoinProtocolCodec;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * Decodes a received {@link ByteBuf} into a case class that represents Bitcoin protocol message.
 */
@ChannelHandler.Sharable
public class BitcoinProtocolDecoder extends MessageToMessageDecoder<ByteBuf> {


    /**
     * An incomplete message, which needs to receive more data to construct a complete message.
     *
     * This happens situations as follow.
     * (1) when the data received is less than 24 bytes, which is the length for the header of bitcoin message.
     * (2) when we received less than the length specified at the payload length( BitcoinMessageEnvelope.length ).
     */


    /**
     * Creates a new instance with the current system character set.
     */

    private BitcoinProtocolCodec codec = new BitcoinProtocolCodec( new BitcoinProtocol() );

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> out) throws Exception {
        codec.decode(byteBuf, out);

        if (out.size() > 0) {
            System.out.println("decoded : " + out.size());
        } else {
            System.out.println("nothing decoded.");
        }
    }
}
