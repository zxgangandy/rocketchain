package com.rocketchain.script.ops;

/**
 * OP_AND(0x84) : Disabled (Boolean AND of two top items)
 */
public class OpAnd implements DisabledScriptOp {
    @Override
    public OpCode opCode() {
        return new OpCode((short) 0x84);
    }
}
