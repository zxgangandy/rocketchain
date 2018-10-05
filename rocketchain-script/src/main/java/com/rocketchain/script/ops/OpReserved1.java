package com.rocketchain.script.ops;

/** OP_RESERVED1(0x89) : Halt - Invalid transaction unless found in an unexecuted OP_IF clause
 */
public class OpReserved1 implements InvalidScriptOpIfExecuted {
    @Override
    public OpCode opCode() {
        return new OpCode((short)0x89);
    }
}
