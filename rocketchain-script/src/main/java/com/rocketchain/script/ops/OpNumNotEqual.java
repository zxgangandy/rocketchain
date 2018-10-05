package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;

/**
 * OP_NUMNOTEQUAL(0x9e) : Return TRUE if top two items are not equal numbers
 */
public class OpNumNotEqual implements Arithmetic {
    @Override
    public OpCode opCode() {
        return new OpCode((short) 0x9e);
    }

    @Override
    public void execute(ScriptEnvironment env) {
        binaryIntOperation(env, (l, r) -> (l != r) ? 1L : 0L);
    }
}
