package com.rocketchain.codec.primitive;

import com.rocketchain.codec.Codec;
import com.rocketchain.codec.CodecInputOutputStream;

public class BooleanCodec implements Codec<Boolean> {
    @Override
    public Boolean transcode(CodecInputOutputStream io, Boolean obj) {
        if (io.getInput()) {
            return (io.getByteBuf().readByte() != (byte) 0);
        } else {
            io.getByteBuf().writeByte( obj ?  1 : 0 );
            return null;
        }
    }
}
