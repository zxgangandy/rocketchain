package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;

/**
 * OP_BOOLOR(0x9b) : Boolean OR of top two items
 */
public class OpBoolOr implements Arithmetic {
    @Override
    public OpCode opCode() {
        return new OpCode((short) 0x9b);
    }

    @Override
    public void execute(ScriptEnvironment env) {
        binaryIntOperation(env, (l, r) -> (l != 0L || r != 0L) ? 1L : 0L);
    }
}
