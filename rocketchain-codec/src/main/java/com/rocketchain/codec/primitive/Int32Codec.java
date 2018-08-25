package com.rocketchain.codec.primitive;

import com.rocketchain.codec.Codec;
import com.rocketchain.codec.CodecInputOutputStream;

public class Int32Codec implements Codec<Integer> {
    @Override
    public Integer transcode(CodecInputOutputStream io, Integer obj) {
        if (io.getInput()) {
            return io.getByteBuf().readInt();
        } else {
            io.getByteBuf().writeInt(obj);
            return null;
        }
    }
}
