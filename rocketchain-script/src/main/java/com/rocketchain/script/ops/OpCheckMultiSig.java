package com.rocketchain.script.ops;

import com.rocketchain.proto.Script;
import com.rocketchain.script.ScriptEnvironment;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

/** OP_CHECKMULTISIG(0xae) : Run CHECKSIG for each pair of signature and public key provided. All must match. Bug in implementation pops an extra value, prefix with OP_NOP as workaround
 *  Before : x sig1 sig2 ... <number of signatures> pub1 pub2 <number of public keys>
 *  After : True if multisig check passes. False otherwise.
 *
 *  Compares the first signature against each public key until it finds an ECDSA match.
 *  Starting with the subsequent public key, it compares the second signature against each remaining public key
 *  until it finds an ECDSA match. The process is repeated until all signatures have been checked or
 *  not enough public keys remain to produce a successful result. All signatures need to match a public key.
 *  Because public keys are not checked again if they fail any signature comparison,
 *  signatures must be placed in the scriptSig using the same order
 *  as their corresponding public keys were placed in the scriptPubKey or redeemScript.
 *  If all signatures are valid, 1 is returned, 0 otherwise.
 *  Due to a bug, one extra unused value is removed from the stack.
 *
 *  Ex> An example of 2 of 3 multisig.
 *
 *  <Unlocking Script ; provided by a spending transaction>
 *  ScriptOpList(operations=
 *    Array(
 *      Op0(), // Dummy value. we need it because the reference implementation has a bug
 *             // popping an additional item from the stack at the end of OP_CHECKMULTISIG execution.
 *      OpPush(72,ScriptBytes(bytes("sig2"))), // Second signature.
 *      OpPush(72,ScriptBytes(bytes("sig1")))  // First signature.
 *    )
 *  ),
 *
 *  <Locking Script; attached to UTXO >
 *  ScriptOpList(operations=
 *    Array(
 *      OpNum(2), // The number of required signatures.
 *      OpPush(33,ScriptBytes(bytes("pub key3"))), // Third public key
 *      OpPush(33,ScriptBytes(bytes("pub key2"))), // Second public key
 *      OpPush(33,ScriptBytes(bytes("pub key1"))), // First public key
 *      OpNum(3), // The number of public keys.
 *      OpCheckMultiSig(Script(bytes("..."))) // The OP_CHECKMULTISIG signature.
 *    )
 *  )
 *
 *  The order of signatures should match the order of public keys.
 *  ex> If sig1 matches pub key2, sig2 can not match pub key 1, but it can match pub key 3.
 *      If sig1 matches pub key1, sig2 can either match pub key 2 or 3.
 *
 */
public class OpCheckMultiSig extends CheckSig {

    private Script script;

    public OpCheckMultiSig() {
        this(null);
    }

    public OpCheckMultiSig(Script script) {
        this.script = script;
    }

    @Override
    public void execute(ScriptEnvironment env) {
        if (script == null) throw new AssertionError();

        checkMultiSig(script, env);
    }

    @Override
    public Pair<ScriptOp, Integer> create(Script script, int offset) {
        return new MutablePair<>(new OpCheckMultiSig(script), 0);
    }
}