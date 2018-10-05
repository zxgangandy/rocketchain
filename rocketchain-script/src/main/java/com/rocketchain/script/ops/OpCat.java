package com.rocketchain.script.ops;

/** OP_CAT(0x7e) : Disabled (concatenates top two items)
 */
public class OpCat implements DisabledScriptOp{
    @Override
    public OpCode opCode() {
        return new OpCode((short)0x7e);
    }
}
