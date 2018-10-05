package com.rocketchain.script.ops;

/** OP_LEFT(0x80) : Disabled (returns left substring)
 */
public class OpLeft implements DisabledScriptOp {
    @Override
    public OpCode opCode() {
        return new OpCode((short)0x80);
    }
}
