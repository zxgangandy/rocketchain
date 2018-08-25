package com.rocketchain.codec.primitive;

import com.rocketchain.codec.Codec;
import com.rocketchain.codec.CodecInputOutputStream;

public class Int64LCodec implements Codec<Long> {
    @Override
    public Long transcode(CodecInputOutputStream io, Long obj) {
        if (io.getInput()) {
            return io.getByteBuf().readLongLE();
        } else {
            io.getByteBuf().writeLongLE(obj);
            return null;
        }
    }
}
