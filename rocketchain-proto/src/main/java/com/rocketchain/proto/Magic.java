package com.rocketchain.proto;

import com.rocketchain.utils.lang.Bytes;
import com.rocketchain.utils.lang.HexUtil;

import java.util.Arrays;

public class Magic {
    private Bytes value;

    public Magic(Bytes value) {
        this.value = value;
    }

    public static final int VALUE_SIZE = 4;

    public static Magic MAIN = fromHex("d9b4bef9");
    public static Magic TESTNET = fromHex("DAB5BFFA");
    public static Magic TESTNET3 = fromHex("0709110B");
    public static Magic NAMECOIN = fromHex("FEB4BEF9");

    public static Magic fromHex(String hexString) {
        return new Magic(Bytes.from(hexString));
    }

    @Override
    public boolean equals(Object obj) {
        return Arrays.equals(value.getArray(), ((Magic)obj).value.getArray());
    }

    @Override
    public String toString() {
        return "Magic{" +
                HexUtil.byteArrayToHexString(value.getArray()) +
                '}';
    }

    public Bytes getValue() {
        return value;
    }
}
