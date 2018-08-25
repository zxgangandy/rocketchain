package com.rocketchain.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;

public interface Codec<T> {
    /**
     * Implements both encoding and decoding object.
     * InputOutputObject holds either BlockDataInputStream or BlockDataOutputStream.
     * If it holds BlockDataInputStream, it decodes the transcodable from the input stream.
     * If it holds BlockDataOutputStream, it encodes the transcodable into the output stream.
     * <p>
     * Why? If we have separate encode/decode function for an object,
     * we will have redundant code for each field we encode and decode in the two functions.
     */
    T transcode(CodecInputOutputStream io, T obj);

    default T decode(byte[] data) {
        return decode(Unpooled.wrappedBuffer(data));
    }

    default T decode(ByteBuf byteBuf) {
        return transcode(new CodecInputOutputStream(byteBuf, true), null);
    }

    default byte[] encode(T value) {
        return ByteBufUtil.getBytes(encodeAsByteBuf(value));
    }

    default ByteBuf encodeAsByteBuf(T value) {
        ByteBuf byteBuf = Unpooled.buffer();
        transcode(new CodecInputOutputStream(byteBuf, false), value);
        return byteBuf;
    }
}
