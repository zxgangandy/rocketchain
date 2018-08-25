package com.rocketchain.codec.primitive;

import com.rocketchain.codec.Codec;
import com.rocketchain.codec.CodecInputOutputStream;

import java.nio.charset.Charset;

public class CStringCodec implements Codec<String> {
    private Charset charset;
    private CByteArrayCodec Codec = new CByteArrayCodec();

    public CStringCodec(Charset charset) {
        this.charset = charset;
    }

    @Override
    public String transcode(CodecInputOutputStream io, String obj) {
        if (io.getInput()) {
            byte[] bytes = Codec.transcode(io, null);

            return new String(bytes, charset);
        } else {
            byte[] byteArray = obj.getBytes(charset);

            Codec.transcode(io, byteArray);

            return null;
        }
    }
}
