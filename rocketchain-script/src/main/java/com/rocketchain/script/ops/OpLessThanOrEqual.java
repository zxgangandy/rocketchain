package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;

/**
 * OP_LESSTHANOREQUAL(0xa1) : Return TRUE if second item is less than or equal to top item
 */
public class OpLessThanOrEqual implements Arithmetic {
    @Override
    public OpCode opCode() {
        return new OpCode((short) 0xa1);
    }

    @Override
    public void execute(ScriptEnvironment env) {
        binaryIntOperation(env, (l, r) -> (l <= r) ? 1L : 0L);
    }
}
