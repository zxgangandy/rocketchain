package com.rocketchain.script.ops;

/** OP_RESERVED2(0x8a) : Halt - Invalid transaction unless found in an unexecuted OP_IF clause
 */
public class OpReserved2 implements InvalidScriptOpIfExecuted{
    @Override
    public OpCode opCode() {
        return new OpCode((short)0x8a);
    }
}
