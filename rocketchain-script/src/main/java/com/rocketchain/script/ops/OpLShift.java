package com.rocketchain.script.ops;

/** OP_LSHIFT(0x98) : Disabled (shift second item left by first item number of bits)
 */
public class OpLShift implements DisabledScriptOp {
    @Override
    public OpCode opCode() {
        return new OpCode((short)0x98);
    }
}
