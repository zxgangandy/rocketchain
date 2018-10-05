package com.rocketchain.script.ops;

/** OP_VERNOTIF(0x66) : Halt - Invalid transaction
 */
public class OpVerNotIf implements AlwaysInvalidScriptOp {
    @Override
    public OpCode opCode() {
        return new OpCode((short)0x66);
    }
}
