package com.rocketchain.script.ops;

/** OP_MUL(0x95) : Disabled (multiply top two items)
 */
public class OpMul implements DisabledScriptOp{
    @Override
    public OpCode opCode() {
        return new OpCode((short)0x95);
    }
}
