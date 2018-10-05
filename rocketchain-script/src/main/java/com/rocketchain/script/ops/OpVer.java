package com.rocketchain.script.ops;

/** P_VER(0x62) : Halt - Invalid transaction unless found in an unexecuted OP_IF clause
 */
public class OpVer implements InvalidScriptOpIfExecuted{
    @Override
    public OpCode opCode() {
        return new OpCode((short)0x62);
    }
}
