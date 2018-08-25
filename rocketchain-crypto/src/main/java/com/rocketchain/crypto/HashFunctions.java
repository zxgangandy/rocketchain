package com.rocketchain.crypto;

import com.rocketchain.utils.lang.Bytes;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class HashFunctions {

    /**
     * Return SHA256(SHA256(x)) hash
     *
     * @param input
     * @return
     */
    public static Hash256 hash256(byte[] input) {
        return new Hash256(sha256(sha256(input).getValue().getArray()).getValue());
    }

    public static SHA256 sha256(byte[] input) {
        MessageDigest sha256md = null;
        try {
            sha256md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return new SHA256(new Bytes(sha256md.digest(input)));
    }
}
