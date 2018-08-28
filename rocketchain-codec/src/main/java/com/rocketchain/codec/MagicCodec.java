package com.rocketchain.codec;

import com.rocketchain.codec.primitive.Codecs;
import com.rocketchain.proto.Magic;
import com.rocketchain.utils.lang.Bytes;

public class MagicCodec implements Codec<Magic> {

    @Override
    public Magic transcode(CodecInputOutputStream io, Magic obj) {

        Bytes bytes = obj == null ? null : obj.getValue();
        byte[] array = bytes == null ? null : bytes.getArray();
        byte[] value = Codecs.fixedReversedByteArray(Magic.VALUE_SIZE).transcode(io, array);
        if (io.getInput()) {
            return new  Magic(new Bytes(value));
        }
        return null;
    }
}
