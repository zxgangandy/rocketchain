package com.rocketchain.script.ops;

/** OP_VERIF(0x65) : Halt - Invalid transaction
 */
public class OpVerIf implements AlwaysInvalidScriptOp {
    @Override
    public OpCode opCode() {
        return new OpCode((short)0x65);
    }
}
