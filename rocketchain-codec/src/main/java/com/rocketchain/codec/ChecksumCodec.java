package com.rocketchain.codec;

import com.rocketchain.codec.primitive.Codecs;
import com.rocketchain.proto.Checksum;
import com.rocketchain.utils.lang.Bytes;


public class ChecksumCodec implements Codec<Checksum> {
    @Override
    public Checksum transcode(CodecInputOutputStream io, Checksum obj) {
        Bytes bytes = obj == null ? null : obj.getValue();
        byte[] array = bytes == null ? null : bytes.getArray();
        byte[] value = Codecs.fixedByteArray(Checksum.VALUE_SIZE).transcode(io, array);
        if (io.getInput()) {
            return new Checksum(new Bytes(value));
        }
        return null;
    }
}
