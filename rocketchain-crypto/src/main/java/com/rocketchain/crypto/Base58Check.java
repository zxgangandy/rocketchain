package com.rocketchain.crypto;

import com.google.common.primitives.Bytes;
import com.rocketchain.utils.Base58Util;
import com.rocketchain.utils.exception.ErrorCode;
import com.rocketchain.utils.exception.GeneralException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

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

    /**
     * Decodes Base58 data that has been encoded with a single byte prefix
     *
     * @param encoded encoded data
     * @return a (prefix, data) tuple
     * @throws RuntimeException if the checksum that is part of the encoded data cannot be verified
     */
    public static Pair<Byte, byte[]> decode(String encoded)  {
        byte[] raw = Base58Util.decode(encoded);
        int length = raw.length;
        byte[] versionAndHash  = Arrays.copyOfRange(raw, 0, length -4);
        byte[] checksum  = Arrays.copyOfRange(raw, length -4, length);
        if (!Arrays.equals(checksum, Base58Check.checksum(versionAndHash))) {
            throw new GeneralException(ErrorCode.InvalidChecksum);
        }

        byte[] temp = Arrays.copyOfRange(versionAndHash, 1, versionAndHash.length);
        return new MutablePair<>(versionAndHash[0], temp);
    }
}
