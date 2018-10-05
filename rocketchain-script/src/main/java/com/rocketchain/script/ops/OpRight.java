package com.rocketchain.script.ops;

/** OP_RIGHT(0x81) : Disabled (returns right substring)
 */
public class OpRight implements DisabledScriptOp {
    @Override
    public OpCode opCode() {
        return new OpCode((short)0x81);
    }
}
