package com.rocketchain.proto;

import com.rocketchain.utils.lang.Bytes;
import com.rocketchain.utils.lang.HexUtil;

import java.util.Arrays;

public class Checksum {

    private Bytes value;

    public Checksum(Bytes value) {
        this.value = value;
    }

    public static final int VALUE_SIZE = 4;

    public static Checksum fromHex(String hexString) {
        return new Checksum(Bytes.from(hexString));
    }

    @Override
    public boolean equals(Object obj) {
        return Arrays.equals(value.getArray(), ((Checksum) obj).value.getArray());
    }

    @Override
    public String toString() {
        return "Checksum{" +
                HexUtil.byteArrayToHexString(value.getArray()) +
                '}';
    }

    public Bytes getValue() {
        return value;
    }
}
