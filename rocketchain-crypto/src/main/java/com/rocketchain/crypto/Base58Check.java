package com.rocketchain.crypto;

import com.google.common.primitives.Bytes;
import com.rocketchain.utils.Base58Util;

import java.util.Arrays;

public class Base58Check {

    public static byte[] checksum(byte[] data) {
        byte[] temp = HashFunctions.hash256(data).value.getArray();
        return Arrays.copyOfRange(temp, 0, 4);
    }

    /**
     * Encode data in Base58Check format.
     * For example, to create an address from a public key you could use:
     *
     * @param prefix version prefix (one byte)
     * @param data   date to be encoded
     * @return a Base58 string
     */
    public static String encode(byte prefix, byte[] data) {
        return encode(new byte[]{prefix}, data);
    }

    /**
     * @param prefix version prefix (several bytes, as used with BIP32 ExtendedKeys for example)
     * @param data   data to be encoded
     * @return a Base58 String
     */
    private static String encode(byte[] prefix, byte[] data) {
        byte[] prefixAndData = Bytes.concat(prefix, data);
        return Base58Util.encode(Bytes.concat(prefixAndData, checksum(prefixAndData)));
    }
}
