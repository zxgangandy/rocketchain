package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;

/**
 * OP_WITHIN(0xa5) : Return TRUE if the third item is between the second item (or equal) and first item
 * Returns 1 if x is within the specified range (left-inclusive), 0 otherwise.
 */
public class OpWithin implements Arithmetic {
    @Override
    public OpCode opCode() {
        return new OpCode((short) 0xa5);
    }

    @Override
    public void execute(ScriptEnvironment env) {
        ternaryIntOperation(env, (x, min, max) -> ((min <= x) && (x < max)) ? 1L : 0L);
    }
}
