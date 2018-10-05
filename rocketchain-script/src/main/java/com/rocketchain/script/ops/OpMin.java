package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;

/**
 * OP_MIN(0xa3) : Return the smaller of the two top items
 */
public class OpMin implements Arithmetic {
    @Override
    public OpCode opCode() {
        return new OpCode((short) 0xa3);
    }

    @Override
    public void execute(ScriptEnvironment env) {
        binaryIntOperation(env, (l, r) -> Math.min(l, r));
    }
}
