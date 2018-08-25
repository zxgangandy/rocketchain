package com.rocketchain.codec.primitive;

import com.rocketchain.codec.Codec;
import com.rocketchain.codec.CodecInputOutputStream;
import com.rocketchain.proto.CStringPrefixed;

public class CStringPrefixedCodec<T> implements Codec<CStringPrefixed<T>> {
    private Codec<T> codecT;

    public CStringPrefixedCodec(Codec<T> codecT) {
        this.codecT = codecT;
    }

    @Override
    public CStringPrefixed<T> transcode(CodecInputOutputStream io, CStringPrefixed<T> obj) {
        String prefix = Codecs.CString.transcode(io, obj.getPrefix());
        T data  = codecT.transcode(io, obj.getData());
        if (io.getInput()) {
            return new CStringPrefixed<T>(prefix, data);
        }
        return null;
    }
}
