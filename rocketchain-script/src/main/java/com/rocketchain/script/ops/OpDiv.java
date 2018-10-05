package com.rocketchain.script.ops;

/** OP_DIV(0x96) : Disabled (divide second item by first item)
 */
public class OpDiv implements DisabledScriptOp {
    @Override
    public OpCode opCode() {
        return new OpCode((short)0x96);
    }
}
