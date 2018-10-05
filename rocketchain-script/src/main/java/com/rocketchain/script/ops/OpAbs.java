package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;

/**
 * OP_ABS(0x90) : Change the sign of the top item to positive
 */
public class OpAbs implements Arithmetic {
    @Override
    public OpCode opCode() {
        return new OpCode((short) 0x90);
    }

    @Override
    public void execute(ScriptEnvironment env) {
        unaryIntOperation(env, (it) -> Math.abs(it));
    }
}
