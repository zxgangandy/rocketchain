package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;

import java.math.BigInteger;

public class OpNum implements Constant {
    private int num;

    public OpNum(int num) {
        this.num = num;
    }

    public int getNum() {
        return num;
    }

    // 0x50 is the base value for opCode. The actual op code is calculated by adding byteCount.
    @Override
    public OpCode opCode() {
        return opCodeFromBase(0x50, num);
    }

    @Override
    public void execute(ScriptEnvironment env) {
        assert(2 <= num && num <=16);
        env.getStack().pushInt( BigInteger.valueOf(num));
    }
}
