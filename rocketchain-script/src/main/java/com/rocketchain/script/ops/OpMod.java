package com.rocketchain.script.ops;

/** OP_MOD(0x97) : Disabled (remainder divide second item by first item)
 */
public class OpMod implements DisabledScriptOp{
    @Override
    public OpCode opCode() {
        return new OpCode((short)0x97);
    }
}
