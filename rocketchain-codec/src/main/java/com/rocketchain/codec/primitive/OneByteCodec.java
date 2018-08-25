package com.rocketchain.codec.primitive;

import com.rocketchain.codec.Codec;
import com.rocketchain.codec.CodecInputOutputStream;
import com.rocketchain.proto.OneByte;

public class OneByteCodec implements Codec<OneByte> {
    @Override
    public OneByte transcode(CodecInputOutputStream io, OneByte obj) {
        Byte value = Codecs.Byte.transcode(io, obj.getValue());
        if (io.getInput()) {
            return new OneByte(value);
        }
        return null;
    }
}
