package com.rocketchain.chain.transaction;

import com.rocketchain.crypto.Base58Check;
import com.rocketchain.proto.LockingScript;
import com.rocketchain.utils.exception.ErrorCode;
import com.rocketchain.utils.exception.GeneralException;
import com.rocketchain.utils.lang.Bytes;
import org.apache.commons.lang3.tuple.Pair;


public class CoinAddress implements OutputOwnership {

    private byte version;
    private Bytes publicKeyHash;

    public CoinAddress(byte version, Bytes publicKeyHash) {
        this.version = version;
        this.publicKeyHash = publicKeyHash;
    }

    public byte getVersion() {
        return version;
    }

    public Bytes getPublicKeyHash() {
        return publicKeyHash;
    }

    /** Return the address in base58 encoding format.
     *
     * @return The base 58 check encoded address.
     */
    public String base58()  {
        assert(isValid());
        return Base58Check.encode(version, publicKeyHash.getArray());
    }

    @Override
    public boolean isValid() {
        NetEnv env = NetEnvFactory.get();

        // The public key hash uses RIPEMD160, so it should be 20 bytes.
        if (publicKeyHash.getArray().length != 20) {
            return false;
        } else if (version != env.PubkeyAddressVersion && version != env.ScriptAddressVersion) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public LockingScript lockingScript() {
        return null;
    }

    @Override
    public String stringKey() {
        return null;
    }


    /** Decode an address and create a CoinAddress.
     *
     * @param address The address to decode.
     * @return The decoded CoinAddress.
     */
    public static CoinAddress from(String address )  {
        Pair<Byte, byte[]> pair = Base58Check.decode(address);
        byte versionPrefix = pair.getLeft().byteValue();
        byte[] publicKeyHash = pair.getRight();

        CoinAddress coinAddress = new CoinAddress(versionPrefix, new Bytes(publicKeyHash));
        if (coinAddress.isValid()) {
            return coinAddress;
        } else {
            throw new GeneralException(ErrorCode.RpcInvalidAddress);
        }
    }

    /** Create a CoinAddress from a public key hash.
     *
     * @param publicKeyHash The public key hash. RIPEMD160( SHA256( publicKey ) )
     * @return The created CoinAddress.
     */
    public static CoinAddress from(byte[] publicKeyHash )  {
        // Step 1 : Get the chain environment to get the address version.
        NetEnv chainEnv = NetEnvFactory.get();

        // Step 2 : Create the CoinAddress
        return new CoinAddress(chainEnv.PubkeyAddressVersion, new Bytes(publicKeyHash));
    }


    /** Create a CoinAddress from a private key.
     *
     * @param privateKey The private key to use to generate public key and public key hash for the new coin address.
     * @return The created CoinAddress.
     */
    public static CoinAddress from(PrivateKey privateKey )  {
        // Step 1 : Create a public key.
        PublicKey publicKey  = PublicKey.from(privateKey);

        // Step 2 : Hash the public key.

        // Step 3 : Create an address.
        return from(publicKey.getHash().getValue().getArray());
    }
}
