package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;

/**
 * OP_ADD(0x93) : Pop top two items, add them and push result
 */
public class OpAdd implements Arithmetic {
    @Override
    public OpCode opCode() {
        return new OpCode((short) 0x93);
    }

    @Override
    public void execute(ScriptEnvironment env) {
        binaryIntOperation(env, (l, r) -> l + r);
    }
}
