package com.rocketchain.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.Charset;

public class VariableStringCodec implements Codec<String> {
    private VariableByteBufCodec VariableByteBufCodec;

    public VariableStringCodec(Codec<Long> lengthCodec ) {
        VariableByteBufCodec = new VariableByteBufCodec(lengthCodec);
    }

    @Override
    public String transcode(CodecInputOutputStream io, String obj) {
        if (io.getInput()) {
            ByteBuf byteBuf  = VariableByteBufCodec.transcode(io, null);
            return byteBuf.toString(Charset.forName("UTF-8"));
        } else {
            // BUGBUG : Unnecessary byte array copy happens from string to bytebuf?
            // Need to understand what happens when we write a ByteBuf into another ByteBuf.
            // If no byte array is copied during this process, it is ok.
            byte[] byteArray = obj == null ? null : obj.getBytes(Charset.forName("UTF-8"));
            VariableByteBufCodec.transcode(io, Unpooled.wrappedBuffer(byteArray));
            return null;
        }
    }
}
