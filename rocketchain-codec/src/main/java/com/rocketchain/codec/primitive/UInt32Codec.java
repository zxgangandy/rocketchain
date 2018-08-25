package com.rocketchain.codec.primitive;

import com.rocketchain.codec.Codec;
import com.rocketchain.codec.CodecInputOutputStream;

public class UInt32Codec implements Codec<Long> {
    @Override
    public Long transcode(CodecInputOutputStream io, Long obj) {
        if (io.getInput()) {
            return io.getByteBuf().readUnsignedInt();
        } else {
            //io.getByteBuf().writeUnsignedInt(obj);
            return null;
        }
    }
}
