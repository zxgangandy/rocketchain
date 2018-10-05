package com.rocketchain.script.ops;

/** OP_INVERT(0x83) : Disabled (Flip the bits of the top item)
 */
public class OpInvert implements DisabledScriptOp {
    @Override
    public OpCode opCode() {
        return new OpCode((short)0x83);
    }
}
