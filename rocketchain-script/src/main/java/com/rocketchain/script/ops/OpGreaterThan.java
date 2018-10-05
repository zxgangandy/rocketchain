package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;

/**
 * OP_GREATERTHAN(0xa0) : Return TRUE if second item is greater than top item
 */
public class OpGreaterThan implements Arithmetic {
    @Override
    public OpCode opCode() {
        return new OpCode((short) 0xa0);
    }

    @Override
    public void execute(ScriptEnvironment env) {
        binaryIntOperation(env, (l, r) -> (l > r) ? 1L : 0L);
    }
}
