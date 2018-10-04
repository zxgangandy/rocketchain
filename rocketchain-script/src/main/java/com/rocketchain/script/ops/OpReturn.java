package com.rocketchain.script.ops;

public class OpReturn implements InvalidScriptOpIfExecuted {
    @Override
    public OpCode opCode() {
        return new OpCode((short)0x6a);
    }
}
