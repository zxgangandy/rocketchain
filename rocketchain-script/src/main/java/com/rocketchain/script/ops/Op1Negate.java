package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;

import java.math.BigInteger;

public class Op1Negate implements Constant {

    @Override
    public OpCode opCode() {
        return new  OpCode((short)0x4f);
    }

    @Override
    public void execute(ScriptEnvironment env) {
        env.getStack().pushInt( BigInteger.valueOf(-1));
    }
}
