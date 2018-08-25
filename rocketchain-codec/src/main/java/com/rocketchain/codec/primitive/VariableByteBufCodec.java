package com.rocketchain.codec.primitive;

import com.rocketchain.codec.Codec;
import com.rocketchain.codec.CodecInputOutputStream;
import io.netty.buffer.ByteBuf;

public class VariableByteBufCodec implements Codec<ByteBuf> {

    private Codec<Long> lengthCodec;

    public VariableByteBufCodec(Codec<Long> lengthCodec) {
        this.lengthCodec = lengthCodec;
    }

    public Codec<Long> getLengthCodec() {
        return lengthCodec;
    }

    @Override
    public ByteBuf transcode(CodecInputOutputStream io, ByteBuf obj) {
        Long valueLength = obj == null ? null : Long.valueOf(obj.readableBytes());
        Long length = io.transcode(lengthCodec, valueLength);
        if (io.getInput()) {
            assert (length <= Integer.MAX_VALUE);
            return io.fixedBytes(length.intValue(), null);
        } else {
            assert (valueLength <= Integer.MAX_VALUE);
            io.fixedBytes(valueLength.intValue(), obj);
            return null;
        }
    }
}
