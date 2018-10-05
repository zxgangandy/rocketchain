package com.rocketchain.script.ops;


import com.rocketchain.proto.Script;
import com.rocketchain.script.ScriptEnvironment;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

/** OP_CHECKMULTISIGVERIFY(0xaf) : Same as CHECKMULTISIG, then OP_VERIFY to halt if not TRUE
 */
public class OpCheckMultiSigVerify extends CheckSig {

    private Script script;

    public OpCheckMultiSigVerify() {
        this(null);
    }

    public OpCheckMultiSigVerify(Script script) {
        this.script = script;
    }

    @Override
    public void execute(ScriptEnvironment env) {
        if (script == null) throw new AssertionError();

        checkMultiSig(script, env);
        verify(env);
    }

    @Override
    public Pair<ScriptOp, Integer> create(Script script, int offset) {
        return new MutablePair<>(new OpCheckMultiSigVerify(script), 0);
    }
}