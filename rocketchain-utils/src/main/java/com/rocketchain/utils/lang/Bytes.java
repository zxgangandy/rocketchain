package com.rocketchain.utils.lang;

import java.util.Arrays;

public class Bytes implements Comparable<Bytes> {
    private byte[] array;

    public Bytes(byte[] array) {
        this.array = array;
    }

    @Override
    public int compareTo(Bytes o) {
        if (o == null) {
            return 1;
        }

        return ArrayUtil.compare(array, o.getArray());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(array);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof Bytes) {
            return Arrays.equals(array, ((Bytes) obj).getArray());
        } else {
            return false;
        }
    }

    public byte[] getArray() {
        return array;
    }

    public static Bytes from(String hexString) {
        return new Bytes(HexUtil.hexStringToByteArray(hexString));
    }

    @Override
    public String toString() {
        return "Bytes{" +
                HexUtil.byteArrayToHexString(array) +
                '}';
    }
}
