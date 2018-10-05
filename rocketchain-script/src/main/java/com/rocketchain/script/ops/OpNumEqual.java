package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;

/**
 * OP_NUMEQUAL(0x9c) : Return TRUE if top two items are equal numbers
 */
public class OpNumEqual implements Arithmetic {
    @Override
    public OpCode opCode() {
        return new OpCode((short) 0x9c);
    }

    @Override
    public void execute(ScriptEnvironment env) {
        binaryIntOperation(env, (l, r) -> (l == r) ? 1L : 0L);
    }
}
