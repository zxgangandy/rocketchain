package com.rocketchain.codec.primitive;

import com.rocketchain.codec.Codec;
import com.rocketchain.codec.CodecInputOutputStream;
import org.apache.commons.lang3.ArrayUtils;

public class FixedReversedByteArrayCodec implements Codec<byte[]> {

    private int length;

    public FixedReversedByteArrayCodec(int length) {
        this.length = length;
    }

    @Override
    public byte[] transcode(CodecInputOutputStream io, byte[] obj) {

        byte[] temp;
        if (obj != null) {
            temp = obj;
            ArrayUtils.reverse(temp);
        } else {
            temp = null;
        }

        byte[] codec = new FixedByteArrayCodec(length).transcode(io, temp);

        if (codec != null) {
            ArrayUtils.reverse(codec);
        }

        return codec;
    }
}
