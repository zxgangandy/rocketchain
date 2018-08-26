package com.rocketchain.codec.primitive;

import com.rocketchain.codec.Codec;
import com.rocketchain.codec.CodecInputOutputStream;
import com.rocketchain.utils.net.ByteUtil;

public class UInt16Codec implements Codec<Integer> {
    @Override
    public Integer transcode(CodecInputOutputStream io, Integer obj) {
        if (io.getInput()) {
            return io.getByteBuf().readUnsignedShort();
        } else {
            if (obj != null) {
                ByteUtil.writeUnsignedShort(io.getByteBuf(), obj);
            }
            return null;
        }
    }
}
