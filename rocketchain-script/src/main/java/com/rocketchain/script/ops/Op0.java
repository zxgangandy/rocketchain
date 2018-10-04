package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;

/** OP_0 or OP_FALSE(0x00) : An empty array is pushed onto the stack
 */
public class Op0 implements Constant {
    @Override
    public OpCode opCode() {
        return new OpCode((short)0x00);
    }

    @Override
    public void execute(ScriptEnvironment env) {
        pushFalse(env);
    }
}
