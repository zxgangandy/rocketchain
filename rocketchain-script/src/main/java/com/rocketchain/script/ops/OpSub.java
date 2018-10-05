package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;

/**
 * OP_SUB(0x94) : Pop top two items, subtract first from second, push result
 */
public class OpSub implements Arithmetic {
    @Override
    public OpCode opCode() {
        return new OpCode((short) 0x94);
    }

    @Override
    public void execute(ScriptEnvironment env) {
        binaryIntOperation(env, (l, r) -> l - r);
    }
}
