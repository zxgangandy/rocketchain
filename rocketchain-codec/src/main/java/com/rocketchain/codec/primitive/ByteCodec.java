package com.rocketchain.codec.primitive;

import com.rocketchain.codec.Codec;
import com.rocketchain.codec.CodecInputOutputStream;

public class ByteCodec implements Codec<Byte> {

    @Override
    public Byte transcode(CodecInputOutputStream io, Byte obj) {
        if (io.getInput()) {
            return io.getByteBuf().readByte();
        } else {
            io.getByteBuf().writeByte(obj.intValue());
            return null;
        }
    }
}
