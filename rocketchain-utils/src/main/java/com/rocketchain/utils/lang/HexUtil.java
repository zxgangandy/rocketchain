package com.rocketchain.utils.lang;

public class HexUtil {
    private static final String HEXES = "0123456789ABCDEF";
    private static final String HEX_HEADER = "0x";

    /**
     * Converts the given hex string into a byte array.
     *
     * @param value Hex string to convert to byte array.
     * @return Byte array of the given hex string.
     * @throws NullPointerException if {@code value == null}.
     * @see #byteArrayToHexString(byte[])
     */
    public static byte[] hexStringToByteArray(String value) {
        if (value == null)
            throw new NullPointerException("Value to convert cannot be null.");

        value = value.trim();
        if (value.startsWith(HEX_HEADER))
            value = value.substring((HEX_HEADER).length());
        int len = value.length();
        if (len % 2 != 0) {
            value = "0" + value;
            len = value.length();
        }
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(value.charAt(i), 16) << 4)
                    + Character.digit(value.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * Converts the given byte into an hex string.
     *
     * @param value Byte to convert to hex string.
     * @return Converted byte to hex string.
     */
    public static String byteToHexString(byte value) {
        final StringBuilder hex = new StringBuilder(2);
        byte b = value;
        hex.append(HEXES.charAt((b & 0xF0) >> 4))
                .append(HEXES.charAt((b & 0x0F)));
        return hex.toString();
    }

    /**
     * Converts the given byte array into an hex string.
     *
     * @param value Byte array to convert to hex string.
     * @return Converted byte array to hex string.
     * @throws NullPointerException if {@code value == null}.
     * @see #hexStringToByteArray(String)
     */
    public static String byteArrayToHexString(byte[] value) {
        if (value == null)
            throw new NullPointerException("Value to convert cannot be null");
        final StringBuilder hex = new StringBuilder(2 * value.length);
        for (final byte b : value) {
            hex.append(HEXES.charAt((b & 0xF0) >> 4))
                    .append(HEXES.charAt((b & 0x0F)));
        }
        return hex.toString();
    }

    /**
     * Converts the given hexadecimal string to a pretty format by splitting the
     * content byte by byte.
     *
     * @param hexString The hexadecimal string to convert.
     * @return The hexadecimal string with pretty format.
     * @throws NullPointerException if {@code hexString == null}.
     * @see #prettyHexString(byte[])
     */
    public static String prettyHexString(String hexString) {
        if (hexString == null)
            throw new NullPointerException("Hexadecimal string cannot be null.");

        String prettyHexString = "";
        if (hexString.length() % 2 != 0)
            hexString = "0" + hexString;
        int iterations = hexString.length() / 2;
        for (int i = 0; i < iterations; i++)
            prettyHexString += hexString.substring(2 * i, 2 * i + 2) + " ";
        return prettyHexString.trim();
    }

    /**
     * Converts the given byte array into an hex string and retrieves it
     * in pretty format by splitting the content byte by byte.
     *
     * @param value The byte array to convert to pretty hex string.
     * @return The hexadecimal pretty string.
     * @throws NullPointerException if {@code value == null}.
     * @see #prettyHexString(String)
     */
    public static String prettyHexString(byte[] value) {
        return prettyHexString(byteArrayToHexString(value));
    }

}
