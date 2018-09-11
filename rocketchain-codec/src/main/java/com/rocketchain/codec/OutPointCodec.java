package com.rocketchain.codec;

import com.rocketchain.codec.primitive.Codecs;
import com.rocketchain.proto.Hash;
import com.rocketchain.proto.OutPoint;

public class OutPointCodec implements Codec<OutPoint> {
    @Override
    public OutPoint transcode(CodecInputOutputStream io, OutPoint obj) {

        // Note that we are not using the reverseCodec here.
        // OutPointCodec is for writing keys and values on the wallet database, not for communicating with peers.
        Hash transactionHash = new HashCodec().transcode(io,  obj == null ? null : obj.getTransactionHash());
        Integer outputIndex = new Codecs().Int32L.transcode(io,  obj == null ? null : obj.getOutputIndex());

        if (io.getInput()) {
            return new OutPoint(transactionHash, outputIndex);
        }

        return null;
    }
}
