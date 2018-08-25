package com.rocketchain.proto;


import com.rocketchain.utils.lang.Bytes;
import com.rocketchain.utils.lang.HexUtil;
import com.rocketchain.utils.lang.Utils;

import java.math.BigInteger;

public class Hash implements Comparable<Hash> {
    private Bytes value;

    public static Hash ALL_ZERO = from("0000000000000000000000000000000000000000000000000000000000000000");

    public Hash(Bytes value) {
        this.value = value;

        assert (value.getArray().length > 0);
    }

    public static Hash from(String hexString) {
        return new Hash(new Bytes(HexUtil.hexStringToByteArray(hexString)));
    }


    @Override
    public int compareTo(Hash o) {
        BigInteger value1 = Utils.bytesToBigInteger(this.value.getArray());
        BigInteger value2 = Utils.bytesToBigInteger(o.getValue().getArray());

        return value1.compareTo(value2);
    }

    public boolean isAllZero() {
        int i = 0;
        int valueLength = value.getArray().length;
        while (i < valueLength && value.getArray()[i] == 0) {
            i += 1;
        }
        return i == valueLength;
    }

    public Bytes getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Hash{" +
                HexUtil.byteArrayToHexString(value.getArray()) +
                '}';
    }
}
