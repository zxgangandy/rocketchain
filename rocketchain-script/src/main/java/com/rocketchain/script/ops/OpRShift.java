package com.rocketchain.script.ops;

/** OP_RSHIFT(0x99) : Disabled (shift second item right by first item number of bits)
 */
public class OpRShift implements DisabledScriptOp{
    @Override
    public OpCode opCode() {
        return new OpCode((short)0x99);
    }
}
