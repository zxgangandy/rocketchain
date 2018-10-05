package com.rocketchain.script.ops;

import com.rocketchain.proto.Script;
import com.rocketchain.script.ScriptEnvironment;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

/** OP_CHECKSIGVERIFY(0xad) : Same as CHECKSIG, then OP_VERIFY to halt if not TRUE
 */
public class OpCheckSigVerify extends CheckSig {

    private Script script;

    public OpCheckSigVerify() {
        this(null);
    }

    public OpCheckSigVerify(Script script) {
        this.script = script;
    }

    @Override
    public void execute(ScriptEnvironment env) {
        if (script == null) throw new AssertionError();

        checkSig(script, env);
        verify(env);
    }

    @Override
    public Pair<ScriptOp, Integer> create(Script script, int offset) {
        return new MutablePair<>(new OpCheckSigVerify(script), 0);
    }
}
