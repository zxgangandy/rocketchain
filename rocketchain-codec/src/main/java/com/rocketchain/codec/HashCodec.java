package com.rocketchain.codec;

import com.rocketchain.codec.primitive.Codecs;
import com.rocketchain.codec.primitive.FixedReversedByteArrayCodec;
import com.rocketchain.proto.Hash;
import com.rocketchain.utils.lang.Bytes;


public class HashCodec implements Codec<Hash> {

    private FixedReversedByteArrayCodec HashValueCodec = Codecs.fixedReversedByteArray(32);

    @Override
    public Hash transcode(CodecInputOutputStream io, Hash obj) {
        Bytes bytes = obj == null ? null : obj.getValue();
        byte[] byteValue =  bytes == null ? null : bytes.getArray();
        byte[] value = io.transcode( HashValueCodec, byteValue);

        if (io.getInput()) {
            return new Hash(new Bytes(value));
        } else {
            return null;
        }
    }
}
