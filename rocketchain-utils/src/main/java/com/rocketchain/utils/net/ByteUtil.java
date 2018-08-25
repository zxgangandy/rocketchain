package com.rocketchain.utils.net;

import io.netty.buffer.ByteBuf;

public class ByteUtil {


    private static final long UINT_MAX = 4294967295L;
    private static final int USHORT_MAX = 65535;



    public static void writeUnsignedIntLE(ByteBuf byteBuf, long value ) {
        assert (value <= UINT_MAX);
        assert (value >= 0L);

        byteBuf.writeByte((int)(0xFFL & value));
        byteBuf.writeByte((int)(0xFFL & (value >> 8)));
        byteBuf.writeByte((int)(0xFFL & (value >> 16)));
        byteBuf.writeByte((int)(0xFFL & (value >> 24)));
    }



    public static void writeUnsignedInt(ByteBuf byteBuf,long value ) {
        assert (value <= UINT_MAX);
        assert (value >= 0L);

        byteBuf.writeByte((int)(0xFFL & (value >> 24)));
        byteBuf.writeByte((int)(0xFFL & (value >> 16)));
        byteBuf.writeByte((int)(0xFFL & (value >> 8)));
        byteBuf.writeByte((int)(0xFFL &  value));
    }



    public static void writeUnsignedShortLE(ByteBuf byteBuf, int value) {
        assert (value <= USHORT_MAX);
        assert (value >= 0);

        byteBuf.writeByte((0xFF & value));
        byteBuf.writeByte((0xFF & (value >> 8)));
    }



    public static void writeUnsignedShort(ByteBuf byteBuf,int value ) {
        assert (value <= USHORT_MAX);
        assert (value >= 0);

        byteBuf.writeByte((0xFF &(value >> 8)));
        byteBuf.writeByte((0xFF & value));
    }
}
