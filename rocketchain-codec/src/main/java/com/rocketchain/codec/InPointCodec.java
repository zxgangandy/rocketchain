package com.rocketchain.codec;

import com.rocketchain.codec.primitive.Codecs;
import com.rocketchain.proto.Hash;
import com.rocketchain.proto.InPoint;

public class InPointCodec implements Codec<InPoint> {
    @Override
    public InPoint transcode(CodecInputOutputStream io, InPoint obj) {
        Hash transactionHash = new HashCodec().transcode(io, obj.getTransactionHash());
        Integer inputIndex = Codecs.Int32L.transcode(io, obj.getInputIndex());

        if (io.getInput()) {
            return new InPoint(
                    transactionHash,
                    inputIndex);
        }
        return null;
    }
}
