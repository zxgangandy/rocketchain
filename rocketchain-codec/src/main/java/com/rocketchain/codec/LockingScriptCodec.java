package com.rocketchain.codec;

import com.rocketchain.codec.primitive.Codecs;
import com.rocketchain.proto.LockingScript;
import com.rocketchain.utils.lang.Bytes;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;


public class LockingScriptCodec implements Codec<LockingScript> {


    @Override
    public LockingScript transcode(CodecInputOutputStream io, LockingScript obj) {

        ByteBuf byteBufObj;
        if (obj == null) {
            byteBufObj = null;
        } else {
            byteBufObj = Unpooled.wrappedBuffer(obj.getData().getArray());
        }

        ByteBuf byteBuf = Codecs.VariableByteBuf.transcode(io, byteBufObj);

        if (io.getInput()) {
            //println("${HexUtil.hex(byteBuf!!.toByteArray())}")
            return new LockingScript(
                    new Bytes(ByteBufUtil.getBytes(byteBuf)));
        }
        return null;
    }
}
