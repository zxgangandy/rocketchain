package com.rocketchain.codec;

import com.rocketchain.codec.primitive.Codecs;
import com.rocketchain.proto.TransactionCount;

public class TransactionCountCodec implements Codec<TransactionCount> {
    @Override
    public TransactionCount transcode(CodecInputOutputStream io, TransactionCount obj) {
        Long count = Codecs.VariableInt.transcode(io, obj == null ? null : obj.getCount());
        if (io.getInput()) {
            return new TransactionCount(count);
        }
        return null;
    }
}
