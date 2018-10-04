package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;

public class Op1 implements Constant {
    @Override
    public OpCode opCode() {
        return new  OpCode((short)0x51);
    }

    @Override
    public void execute(ScriptEnvironment env) {
        pushTrue(env);
    }
}
