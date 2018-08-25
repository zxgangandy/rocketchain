package com.rocketchain.codec.primitive;

import com.rocketchain.codec.Codec;
import com.rocketchain.codec.CodecInputOutputStream;
import io.netty.buffer.ByteBuf;

public class FixedByteBufCodec implements Codec<ByteBuf> {
    private int length;

    public FixedByteBufCodec(int length) {
        this.length = length;
    }

    @Override
    public ByteBuf transcode(CodecInputOutputStream io, ByteBuf obj) {
        return io.fixedBytes(length, obj);
    }
}
