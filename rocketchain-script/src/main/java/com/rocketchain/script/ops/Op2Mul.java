package com.rocketchain.script.ops;

/** OP_2MUL(0x8d) : Disabled (multiply top item by 2)
 */
public class Op2Mul implements DisabledScriptOp {
    @Override
    public OpCode opCode() {
        return new OpCode((short)0x8d);
    }
}
