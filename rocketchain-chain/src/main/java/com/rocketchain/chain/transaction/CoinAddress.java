package com.rocketchain.chain.transaction;

import com.rocketchain.proto.LockingScript;
import com.rocketchain.utils.lang.Bytes;

public class CoinAddress implements OutputOwnership {

    private byte version;
    private Bytes publicKeyHash;

    public CoinAddress(byte version, Bytes publicKeyHash) {
        this.version = version;
        this.publicKeyHash = publicKeyHash;
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
}
