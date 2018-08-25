package com.rocketchain.codec.primitive;

import com.rocketchain.codec.Codec;
import com.rocketchain.codec.CodecInputOutputStream;

public class Int32LCodec implements Codec<Integer> {
    @Override
    public Integer transcode(CodecInputOutputStream io, Integer obj) {
        if (io.getInput()) {
            return io.getByteBuf().readIntLE();
        } else {
            io.getByteBuf().writeIntLE(obj);
            return null;
        }
    }
}
