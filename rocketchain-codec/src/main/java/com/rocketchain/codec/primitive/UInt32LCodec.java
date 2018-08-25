package com.rocketchain.codec.primitive;

import com.rocketchain.codec.Codec;
import com.rocketchain.codec.CodecInputOutputStream;
import com.rocketchain.utils.net.ByteUtil;

public class UInt32LCodec implements Codec<Long> {
    @Override
    public Long transcode(CodecInputOutputStream io, Long obj) {
        if (io.getInput()) {
            return io.getByteBuf().readUnsignedIntLE();
        } else {
            ByteUtil.writeUnsignedIntLE(io.getByteBuf(), obj);
            return null;
        }
    }
}
