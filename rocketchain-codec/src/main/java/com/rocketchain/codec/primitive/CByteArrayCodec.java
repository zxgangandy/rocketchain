package com.rocketchain.codec.primitive;

import com.google.common.primitives.Bytes;
import com.rocketchain.codec.Codec;
import com.rocketchain.codec.CodecInputOutputStream;

import java.util.ArrayList;
import java.util.List;

public class CByteArrayCodec implements Codec<byte[]> {
    @Override
    public byte[] transcode(CodecInputOutputStream io, byte[] obj) {
        if (io.getInput()) {
            List<Byte> bytes = new ArrayList<>();
            byte b;
            while(true) {
                b = io.getByteBuf().readByte();
                if (b == 0) {
                    break;
                }
                bytes.add(b);
            }

            return Bytes.toArray(bytes);
        } else {
            io.getByteBuf().writeBytes(obj);
            io.getByteBuf().writeByte(0);

            return null;
        }
    }
}
