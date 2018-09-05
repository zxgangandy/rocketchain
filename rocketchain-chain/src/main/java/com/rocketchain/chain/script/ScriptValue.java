package com.rocketchain.chain.script;

import com.rocketchain.utils.exception.ErrorCode;
import com.rocketchain.utils.exception.ScriptEvalException;
import com.rocketchain.utils.lang.Utils;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Arrays;

public abstract class ScriptValue {
    protected byte[] value;

    public abstract ScriptValue copy();


    /**
     * Get a ScriptValue which has a byte array of a given string.
     *
     * @param value The string which will be converted to a byte array.
     * @return The ScriptValue we created.
     */
    public static ScriptValue valueOf(String value) {
        return new ScriptBytes(value.getBytes(Charset.forName("UTF-8")));
    }

    /**
     * Get a ScriptValue which has the given byte array.
     *
     * @param value The byte array.
     * @return The ScriptValue we created.
     */
    public static ScriptValue valueOf(byte[] value) {
        return new ScriptBytes(value);
    }

    /**
     * Get a ScriptValue by copying a specific area of a given byte array.
     *
     * @param source The source byte array
     * @param offset The offset to the source byte array.
     * @param length The number of bytes to copy.
     * @return The ScriptValue that has the given area of the byte array.
     */
    public static ScriptValue valueOf(byte[] source, int offset, int length) {
        byte[] bytes = Arrays.copyOfRange(source, offset, offset + length);
        return new ScriptBytes(bytes);
    }

    /**
     * Get a ScriptValue which has the given long value.
     *
     * @param value The long value.
     * @return The ScriptValue we created.
     */
    public static ScriptValue valueOf(long value) {
        return new ScriptInteger(BigInteger.valueOf(value));
    }

    /**
     * @param value
     * @return
     */
    public static ScriptValue valueOf(BigInteger value) {
        return new ScriptInteger(value);
    }

    public static byte[] staticencodeStackInt(BigInteger value) {
        return Utils.reverseBytes(Utils.encodeMPI(value, false));
    }

    public static BigInteger decodeStackInt(byte[] encoded) {
        if (encoded.length > 4)
            throw new ScriptEvalException(ErrorCode.TooBigScriptInteger, "The integer stack value to decode has more than 4 bytes.");
        return Utils.castToBigInteger(encoded);
    }

    public byte[] getValue() {
        return value;
    }
}
