package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;

/**
 * OP_NOT(0x91) : If the input is 0 or 1, it is flipped. Otherwise the output will be 0.
 */
public class OpNot implements Arithmetic {
    @Override
    public OpCode opCode() {
        return new OpCode((short) 0x91);
    }

    @Override
    public void execute(ScriptEnvironment env) {
        unaryIntOperation(env, (value) -> value == 0L ? 1L : 0L);
    }
}
