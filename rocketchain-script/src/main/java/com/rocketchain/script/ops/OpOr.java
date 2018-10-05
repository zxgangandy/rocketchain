package com.rocketchain.script.ops;

/** OP_OR(0x85) : Disabled (Boolean OR of two top items)
 */
public class OpOr implements DisabledScriptOp{
    @Override
    public OpCode opCode() {
        return new OpCode((short)0x85);
    }
}
