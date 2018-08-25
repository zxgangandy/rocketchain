package com.rocketchain.codec.primitive;

import com.rocketchain.codec.Codec;
import com.rocketchain.codec.CodecInputOutputStream;
import com.rocketchain.utils.net.ByteUtil;
import io.netty.buffer.ByteBuf;

public class VariableIntCodec implements Codec<Long> {
    @Override
    public Long transcode(CodecInputOutputStream io, Long obj) {
        if (io.getInput()) {
            return _decode(io.getByteBuf());
        } else {
            _encode(io.getByteBuf(), obj);
            return null;
        }
    }

    private long _decode(ByteBuf byteBuf) {
        long nextByte = byteBuf.readByte();

        long first = 0xFF & nextByte;

        if (first < 253) {
            // VarInt encoded in 1 byte. 1 data byte (8 bits)[[
            return (long) first;
        } else if (first == 253) {
            // VarInt encoded in 3 bytes. 1 marker + 2 data bytes (16 bits)
            return (long) ((0xFF & nextByte) | ((0xFF & nextByte) << 8));
        } else if (first == 254) {
            // VarInt encoded in 5 bytes. 1 marker + 4 data bytes (32 bits)
            return byteBuf.readUnsignedIntLE();
        } else {
            // VarInt encoded in 9 bytes. 1 marker + 8 data bytes (64 bits)
            return byteBuf.readLongLE();
        }
    }

    private int sizeOf(long value) {
        // if negative, it's actually a very large unsigned long value
        if (value < 0) return 9;// 1 marker + 8 data bytes
        if (value < 253) return 1; // 1 data byte
        if (value <= 0xFFFFL) return 3; // 1 marker + 2 data bytes
        if (value <= 0xFFFFFFFFL) return 5; // 1 marker + 4 data bytes
        return 9; // 1 marker + 8 data bytes
    }

    /**
     * Write a long value as a variable integer format on the stream.
     *
     * @param value The long value to write.
     */
    private void _encode(ByteBuf byteBuf, long value) {
        switch (sizeOf(value)) {
            case 1:
                byteBuf.writeByte((int) (value & 0xFF));
                break;
            case 3:
                byteBuf.writeByte(253);
                byteBuf.writeByte((int) (value & 0xFF));
                byteBuf.writeByte(((int) (value >> 8) & 0xFF));
                break;

            case 5:
                byteBuf.writeByte(254);
                ByteUtil.writeUnsignedIntLE(byteBuf, value);
                break;

            default:
                byteBuf.writeByte(255);
                byteBuf.writeLongLE(value);
        }
    }
}
