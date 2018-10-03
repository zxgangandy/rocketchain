package com.rocketchain.chain.transaction;

import com.rocketchain.crypto.ECKey;
import com.rocketchain.crypto.Hash160;
import com.rocketchain.crypto.HashFunctions;
import org.spongycastle.math.ec.ECPoint;

public class PublicKey {
    private ECPoint point;
    private boolean shouldUseCompressedFormat;

    public PublicKey(ECPoint point) {
        this.point = point;
        this.shouldUseCompressedFormat = false;
    }

    /**
     * The public key.
     * <p>
     * TODO : Test Automation
     *
     * @param shouldUseCompressedFormat true to use the compressed format; false to use the uncompressed format.
     * @param point                     The point on the elliptic curve, which represents a public key.
     */
    public PublicKey(ECPoint point, boolean shouldUseCompressedFormat) {
        this.point = point;
        this.shouldUseCompressedFormat = shouldUseCompressedFormat;
    }

    public ECPoint getPoint() {
        return point;
    }

    public boolean isShouldUseCompressedFormat() {
        return shouldUseCompressedFormat;
    }

    public byte[] encode() {
        return point.getEncoded(shouldUseCompressedFormat);
    }

    /**
     * Get the hash of the public key
     *
     * @return the public key hash.
     */
    public Hash160 getHash() {
        return HashFunctions.hash160(encode());
    }


    /**
     * Get a public key from an encoded one.
     *
     * @param encoded The encoded public key. Can be either a compressed one or uncompressed one.
     * @return The public key.
     */
    public static PublicKey from(byte[] encoded) {
        ECPoint point = ECKey.decodePublicKey(encoded);
        return new PublicKey(point);
    }


    /**
     * Get a public key from a private key.
     *
     * @param privateKey The private key to derive the public key.
     * @return The derived public key.
     */
    public static PublicKey from(PrivateKey privateKey) {
        byte[] encodedPublicKey = ECKey.publicKeyFromPrivate(privateKey.getValue(), false);
        ECPoint point = ECKey.decodePublicKey(encodedPublicKey);
        return new PublicKey(point, privateKey.isForCompressedPublicKey());
    }

}
