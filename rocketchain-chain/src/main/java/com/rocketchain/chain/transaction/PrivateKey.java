package com.rocketchain.chain.transaction;

import com.rocketchain.crypto.Base58Check;
import com.rocketchain.utils.lang.Utils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigInteger;
import java.security.SecureRandom;

public class PrivateKey {

    private byte version;
    private BigInteger value;
    private boolean isForCompressedPublicKey;

    public PrivateKey(byte version, BigInteger value, boolean isForCompressedPublicKey) {
        this.version = version;
        this.value = value;
        this.isForCompressedPublicKey = isForCompressedPublicKey;
    }


    public byte getVersion() {
        return version;
    }

    public BigInteger getValue() {
        return value;
    }

    public boolean isForCompressedPublicKey() {
        return isForCompressedPublicKey;
    }

    /**
     * Return the address in base58 encoding format.
     *
     * @return The base 58 check encoded private key.
     */
    public String base58() {
        byte[] privateKeyBytes = Utils.bigIntegerToBytes(value, 32);
        assert (privateKeyBytes.length == 32);
        return Base58Check.encode(version, privateKeyBytes);
    }


    /**
     * Translate the wallet import format to get a private key from it.
     *
     * @param walletImportFormat A private key in the wallet import format.
     * @return The translated private key.
     */
    public static PrivateKey from(String walletImportFormat) {

        Pair<Byte, byte[]> pair = Base58Check.decode(walletImportFormat);
        byte versionPrefix = pair.getLeft().byteValue();
        byte[] rawPrivateKeyBytes = pair.getRight();

        // TODO : Investigate : Bitcoin allows the private keys whose lengths are not 32

        Pair<Boolean, byte[]> tempPair = rawPrivateKeyBytes.length == 33 ?
                new ImmutablePair(true, ArrayUtils.subarray(rawPrivateKeyBytes, 0, 32)) :
                new ImmutablePair(false, rawPrivateKeyBytes);

        BigInteger privateKeyBigInt = Utils.bytesToBigInteger(tempPair.getRight());
        return new PrivateKey(versionPrefix, privateKeyBigInt, tempPair.getLeft());
    }

    /**
     * Generate a private key.
     * <p>
     * TODO : Test automation.
     *
     * @return The generated private key.
     */
    public static PrivateKey generate() {
        // Step 1 : Generate Random number. The random number is 32 bytes, and the range is [0 ~ 2^256)
        SecureRandom random = new SecureRandom();
        // On Java 8 this hangs. Need more investigation.
//    random.setSeed( random.generateSeed(32) )
        byte[] keyValue = new byte[32];
        assert (keyValue.length == 32);
        random.nextBytes(keyValue);

        // Step 2 : Get the chain environment to get the secret key version.
        NetEnv chainEnv = NetEnvFactory.get();

        // BUGBUG : Use compressed private key by default.

        // Step 3 : Create the private key.
        return new PrivateKey(chainEnv.SecretKeyVersion, Utils.bytesToBigInteger(keyValue), false);
    }

}
