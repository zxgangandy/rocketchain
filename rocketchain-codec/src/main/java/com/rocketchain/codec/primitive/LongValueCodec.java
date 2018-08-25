package com.rocketchain.codec.primitive;

import com.rocketchain.codec.Codec;
import com.rocketchain.codec.CodecInputOutputStream;
import com.rocketchain.proto.LongValue;

public class LongValueCodec implements Codec<LongValue> {
    @Override
    public LongValue transcode(CodecInputOutputStream io, LongValue obj) {
        long value = Codecs.Int64.transcode(io, obj.getValue());
        if (io.getInput()) {
            return new LongValue(value);
        }
        return null;
    }
}
