package com.rocketchain.codec;

import io.netty.buffer.ByteBuf;

public class VariableByteBufCodec implements Codec<ByteBuf> {
    private Codec<Long> lengthCodec;

    public VariableByteBufCodec(Codec<Long> lengthCodec) {
        this.lengthCodec = lengthCodec;
    }

    @Override
    public ByteBuf transcode(CodecInputOutputStream io, ByteBuf obj) {
        Integer valueLength = obj == null ? null : obj.readableBytes();
        Long length  = io.transcode(lengthCodec, obj == null ? null : Long.valueOf(valueLength));
        if (io.getInput()) {
            //assert(length <= Integer.MAX_VALUE );
            return io.fixedBytes(length.intValue(), null);
        } else {
            //assert(valueLength <= Integer.MAX_VALUE );
            io.fixedBytes(valueLength.intValue(), obj);
            return null;
        }
    }
}
