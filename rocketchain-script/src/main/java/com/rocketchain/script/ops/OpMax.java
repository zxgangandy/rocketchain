package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;

/**
 * OP_MAX(0xa4) : Return the larger of the two top items
 */
public class OpMax implements Arithmetic {
    @Override
    public OpCode opCode() {
        return new OpCode((short) 0xa4);
    }

    @Override
    public void execute(ScriptEnvironment env) {
        binaryIntOperation(env, (l, r) -> Math.max(l, r));
    }
}
