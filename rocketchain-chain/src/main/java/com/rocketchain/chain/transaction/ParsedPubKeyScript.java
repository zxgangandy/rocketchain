package com.rocketchain.chain.transaction;


import com.google.common.collect.Lists;
import com.rocketchain.crypto.ECKey;
import com.rocketchain.crypto.Hash160;
import com.rocketchain.crypto.HashFunctions;
import com.rocketchain.proto.LockingScript;
import com.rocketchain.script.ScriptOpList;
import com.rocketchain.script.ScriptParser;
import com.rocketchain.script.ScriptSerializer;
import com.rocketchain.script.ScriptValue;
import com.rocketchain.script.ops.*;
import com.rocketchain.utils.lang.Bytes;
import com.rocketchain.utils.lang.HexUtil;

import java.util.List;


public class ParsedPubKeyScript implements OutputOwnership {
    private ScriptOpList scriptOps ;

    public ParsedPubKeyScript(ScriptOpList scriptOps) {
        this.scriptOps = scriptOps;
    }

    @Override
    public boolean isValid() {
        // TODO : Check if the scriptOps is one of the pubKeyScript patterns for standard transactions.
        return true;
    }

    @Override
    public LockingScript lockingScript() {
        byte[] serializedScript = ScriptSerializer.serialize(scriptOps.getOperations());
        return new LockingScript(new Bytes(serializedScript));
    }

    @Override
    public String stringKey() {
        return HexUtil.byteArrayToHexString(lockingScript().getData().getArray());
    }

    /** Parse a locking script to get the ParsedPubKeyScript.
     *
     * @param lockingScript The locking script to parse.
     * @return The ParsedPubKeyScript that has the parsed locking script.
     */
    public static ParsedPubKeyScript from(LockingScript lockingScript) {
        return new ParsedPubKeyScript( ScriptParser.parse(lockingScript) );
    }

    /** Create a ParsedPubKeyScript from a private key.
     *
     * @param privateKey The private key to use to generate public key and public key hash for the new coin address.
     * @return The created CoinAddress.
     */
    public static ParsedPubKeyScript from(PrivateKey privateKey )  {
        // Step 1 : Create a public key.
        byte[] publicKey  = ECKey.publicKeyFromPrivate(privateKey.getValue(), false /* uncompressed */);

        // Step 2 : Hash the public key.
        Hash160 publicKeyHash  = HashFunctions.hash160(publicKey);

        return from(publicKeyHash.getValue().getArray());
    }

    /** Create a ParsedPubKeyScript from a public key hash.
     *
     * @param publicKeyHash The public key hash. RIPEMD160( SHA256( publicKey ) )
     * @return The created ParsedPubKeyScript.
     */
    public static ParsedPubKeyScript from(byte[] publicKeyHash )  {
        assert(publicKeyHash.length == 20);
        List<ScriptOp> scriptOps = Lists.newArrayList(new OpDup(), new OpHash160(), new OpPush(20, ScriptValue.valueOf(publicKeyHash)),
                new OpEqualVerify(), new OpCheckSig());

        return new  ParsedPubKeyScript(new ScriptOpList(scriptOps));
    }
}
