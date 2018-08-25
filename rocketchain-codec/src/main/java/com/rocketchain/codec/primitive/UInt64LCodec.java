package com.rocketchain.codec.primitive;

import com.rocketchain.codec.Codec;
import com.rocketchain.codec.CodecInputOutputStream;

import java.math.BigInteger;

public class UInt64LCodec implements Codec<BigInteger> {
    private Int64LCodec Int64L = new Int64LCodec();

    @Override
    public BigInteger transcode(CodecInputOutputStream io, BigInteger obj) {
        if (io.getInput()) {
            Long longValue = Int64L.transcode(io, null);
            return longToBigInt(longValue);
        } else {
            Int64L.transcode(io, bigIntToLong(obj));
            return null;
        }
    }


    private BigInteger longToBigInt(long unsignedLong) {
        return (BigInteger.valueOf(unsignedLong >>> 1).shiftLeft(1)).
                add(BigInteger.valueOf(unsignedLong & 1));
    }

    private Long bigIntToLong(BigInteger n )  {
        long smallestBit = (n.and(BigInteger.valueOf(1))).longValue();
        return ((n.shiftRight(1)).longValue() << 1) | smallestBit;
    }
}
