package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;
import com.rocketchain.proto.Script;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

/** OP_CHECKSIG(0xac) : Pop a public key and signature and validate the signature for the transaction’s hashed data, return TRUE if matching
 * Before : <signature> <public key>
 * After  : (1) 1 if the signature is correct.
 *          (2) an empty array if the signature is not correct.
 * Additional input :
 * Followings are additional input values for calculate hash value.
 * With this hash value and the public key, we can verify if the signature is valid.
 *   1. Transaction ( The transaction we are verifying )
 *   2. Transaction input index ( which has an UTXO )
 *   3. A part of script byte array.
 *   3.1 For calulating hash, we use the byte values after the OP_CODESEPARATOR only.
 *   3.2 Also need to remove signature data if exists.
 *   3.3 Also need to remove OP_CODESEPARATOR operation if exists. ( Need more investigation )
 */
public class OpCheckSig extends CheckSig {

    private Script script ;

    public OpCheckSig(Script script) {
        this.script = script;
    }

    @Override
    public OpCode opCode() {
        return new OpCode((short)0xac);
    }

    @Override
    public void execute(ScriptEnvironment env) {
        if(script == null) throw new AssertionError();

        super.checkSig(script, env);
    }

    @Override
    public Pair<ScriptOp, Integer> create(Script script, int offset) {
        return new ImmutablePair<>(new OpCheckSig(script), 0);
    }
}
