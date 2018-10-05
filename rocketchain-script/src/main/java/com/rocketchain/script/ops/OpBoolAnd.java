package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;

/**
 * OP_BOOLAND(0x9a) : Boolean AND of top two items
 */
public class OpBoolAnd implements Arithmetic {
    @Override
    public OpCode opCode() {
        return new OpCode((short) 0x9a);
    }

    @Override
    public void execute(ScriptEnvironment env) {
        binaryIntOperation(env, (l, r) -> (l != 0L && r != 0L) ? 1L : 0L);
    }
}
