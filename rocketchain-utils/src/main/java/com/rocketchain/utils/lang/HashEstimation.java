package com.rocketchain.utils.lang;

import java.math.BigInteger;

public class HashEstimation {
    /** Calculate the estimated number of hash calculations to get a specific hash.
     *
     * @param hashValue The hash to calculate the estimated number of hash calculations to get the given hash.
     *                  ex> How many times should we calculate the block header hash value to get the given hash?
     *                      The returned value is used to get the estimated chain-work.
     * @return The estimated number of hash calculations for the given block.
     */
    public static long getHashCalculations(byte[] hashValue) {
        // Step 2 : Calculate the (estimated) number of hash calculations based on the hash value.
        BigInteger hashValueBigInt = Utils.bytesToBigInteger(hashValue);
        int totalBits = 8 * 32;

        double value = Math.pow(2, totalBits - hashValueBigInt.bitLength());
        return new Double(value).longValue();
    }
}
