package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;

/**
 * OP_GREATERTHANOREQUAL(0xa2) : Return TRUE if second item is great than or equal to top item
 */
public class OpGreaterThanOrEqual implements Arithmetic {
    @Override
    public OpCode opCode() {
        return new OpCode((short) 0xa2);
    }

    @Override
    public void execute(ScriptEnvironment env) {
        binaryIntOperation(env, (l, r) -> (l >= r) ? 1L : 0L);
    }
}
