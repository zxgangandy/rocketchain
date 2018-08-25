package com.rocketchain.codec.primitive;

import com.rocketchain.codec.Codec;
import com.rocketchain.codec.CodecInputOutputStream;

public class Int64Codec implements Codec<Long> {
    @Override
    public Long transcode(CodecInputOutputStream io, Long obj) {
        if (io.getInput()) {
            return io.getByteBuf().readLong();
        } else {
            io.getByteBuf().writeLong(obj);
            return null;
        }
    }
}
