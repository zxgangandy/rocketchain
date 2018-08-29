package com.rocketchain.codec;

import com.rocketchain.codec.primitive.Codecs;
import com.rocketchain.proto.Verack;

public class VerackCodec extends ProtocolMessageCodec<Verack> {
    private ProvideCodec<Verack> codec = Codecs.provide(new Verack());

    public VerackCodec() {
        command = "verack";
        clazz = Verack.class;
    }

    @Override
    public Verack transcode(CodecInputOutputStream io, Verack obj) {
        return codec.transcode(io, obj);
    }
}
