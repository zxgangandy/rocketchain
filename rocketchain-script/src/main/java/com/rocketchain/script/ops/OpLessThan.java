package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;

/**
 * OP_LESSTHAN(0x9f) : Return TRUE if second item is less than top item
 */
public class OpLessThan implements Arithmetic {
    @Override
    public OpCode opCode() {
        return new OpCode((short) 0x9f);
    }

    @Override
    public void execute(ScriptEnvironment env) {
        binaryIntOperation(env, (l, r) -> (l < r) ? 1L : 0L);
    }
}
