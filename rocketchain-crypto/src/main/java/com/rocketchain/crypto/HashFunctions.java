package com.rocketchain.crypto;

import com.rocketchain.utils.lang.Bytes;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import org.spongycastle.crypto.digests.RIPEMD160Digest;


public class HashFunctions {

    /**
     *
     * @param input
     * @return
     */
    public static SHA1 sha1(byte[] input)  {
        MessageDigest sha1md = null;
        try {
            sha1md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return new SHA1( new Bytes( sha1md.digest(input) ) );
    }

    /**
     * Return SHA256(SHA256(x)) hash
     *
     * @param input
     * @return
     */
    public static Hash256 hash256(byte[] input) {
        return new Hash256(sha256(sha256(input).getValue().getArray()).getValue());
    }


    public static SHA256 sha256(byte[] input, int offset, int length) {
        MessageDigest sha256md = null;
        try {
            sha256md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        sha256md.update(input, offset, length);
        return new SHA256(new Bytes(sha256md.digest()));
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

    public static Hash256 hash256(byte[] input, int offset, int length) {
        return new Hash256(sha256(sha256(input, offset, length).getValue().getArray()).getValue());
    }

    /**
     *
     * @param input
     * @return
     */
    public static RIPEMD160 ripemd160(byte[] input)  {
        RIPEMD160Digest md = new RIPEMD160Digest();
        md.update(input, 0, input.length);
        byte[] out = new byte[md.getDigestSize()];
        Arrays.fill(out, (byte)0);
        md.doFinal(out, 0);
        return new RIPEMD160( new Bytes(out) );
    }

    /** Return RIPEMD160(SHA256(x)) hash
     *
     * @param input
     * @return
     */
    public static Hash160 hash160(byte[]  input)  {
        return new Hash160( ripemd160(sha256(input).value.getArray() ).value );
    }
}
