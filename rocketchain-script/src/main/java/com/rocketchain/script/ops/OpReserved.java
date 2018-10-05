package com.rocketchain.script.ops;

/** OP_RESERVED(0x50) : Halt - Invalid transaction unless found in an unexecuted OP_IF clause
 */
public class OpReserved implements InvalidScriptOpIfExecuted {
    @Override
    public OpCode opCode() {
        return new OpCode((short)0x50);
    }
}
