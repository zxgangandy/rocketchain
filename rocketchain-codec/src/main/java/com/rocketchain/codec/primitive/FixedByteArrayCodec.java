package com.rocketchain.codec.primitive;

import com.rocketchain.codec.Codec;
import com.rocketchain.codec.CodecInputOutputStream;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;

public class FixedByteArrayCodec implements Codec<byte[]> {

    private int length;

    public FixedByteArrayCodec(int length) {
        this.length = length;
    }

    @Override
    public byte[] transcode(CodecInputOutputStream io, byte[] obj) {
        if (io.getInput()) {//read
            ByteBuf byteBuf = io.fixedBytes(length, null);
            return ByteBufUtil.getBytes(byteBuf);
        }

        if (obj != null) {//write
            ByteBuf byteBuf = Unpooled.wrappedBuffer(obj);
            io.fixedBytes(length, byteBuf);
        }
        return null;
    }
}
