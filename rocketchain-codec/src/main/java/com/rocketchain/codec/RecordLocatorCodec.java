package com.rocketchain.codec;

import com.rocketchain.codec.primitive.Codecs;
import com.rocketchain.proto.RecordLocator;

public class RecordLocatorCodec implements Codec<RecordLocator> {
    @Override
    public RecordLocator transcode(CodecInputOutputStream io, RecordLocator obj) {
        Long offset = Codecs.Int64L.transcode(io, obj == null ? null : obj.getOffset());
        Integer size = Codecs.Int32L.transcode(io, obj == null ? null : obj.getSize());

        if (io.getInput()) {
            return new RecordLocator(offset, size);
        }
        return null;
    }
}
