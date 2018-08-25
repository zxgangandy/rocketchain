package com.rocketchain.codec;

import com.rocketchain.codec.primitive.Codecs;
import com.rocketchain.proto.UnlockingScript;
import com.rocketchain.utils.lang.Bytes;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;


public class UnlockingScriptCodec implements Codec<UnlockingScript> {
    @Override
    public UnlockingScript transcode(CodecInputOutputStream io, UnlockingScript obj) {
        ByteBuf temBuf;

        if (obj == null) {
            temBuf = null;
        } else {
            temBuf = Unpooled.wrappedBuffer(obj.getData().getArray());
        }

        ByteBuf byteBuf = Codecs.VariableByteBuf.transcode(io, temBuf);

        if (io.getInput()) {
            return new UnlockingScript(new Bytes(ByteBufUtil.getBytes(byteBuf)));
        }
        return null;
    }
}
