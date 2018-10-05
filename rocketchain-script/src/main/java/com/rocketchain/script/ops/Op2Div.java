package com.rocketchain.script.ops;

/** OP_2DIV(0x8e) : Disabled (divide top item by 2)
 */
public class Op2Div implements DisabledScriptOp {
    @Override
    public OpCode opCode() {
        return new OpCode((short)0x8e);
    }
}
