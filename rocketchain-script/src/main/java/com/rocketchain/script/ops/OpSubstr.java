package com.rocketchain.script.ops;

/** OP_SUBSTR(0x7f) : Disabled (returns substring)
 */
public class OpSubstr implements DisabledScriptOp {
    @Override
    public OpCode opCode() {
        return new OpCode((short)0x7f);
    }
}
