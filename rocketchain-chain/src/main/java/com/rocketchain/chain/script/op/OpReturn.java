package com.rocketchain.chain.script.op;

public class OpReturn implements InvalidScriptOpIfExecuted {
    @Override
    public OpCode opCode() {
        return new OpCode((short)0x6a);
    }
}
