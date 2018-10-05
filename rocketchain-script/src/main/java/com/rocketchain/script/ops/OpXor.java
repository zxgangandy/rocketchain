package com.rocketchain.script.ops;

/** OP_XOR(0x86) : Disabled (Boolean XOR of two top items)
 */
public class OpXor implements DisabledScriptOp {
    @Override
    public OpCode opCode() {
        return new OpCode((short)0x86);
    }
}
