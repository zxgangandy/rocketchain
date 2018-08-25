package com.rocketchain.codec;

import io.netty.buffer.ByteBuf;

public class CodecInputOutputStream extends InputOutputStream {
    public CodecInputOutputStream(ByteBuf byteBuf, Boolean isInput) {
        super(byteBuf, isInput);
    }

    public <T> T transcode(Codec<T> codec, T value) {
        return codec.transcode(this, value);
    }
}
