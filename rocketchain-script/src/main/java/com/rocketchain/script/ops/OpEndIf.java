package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;

/** OP_ENDIF(0x68) : End the OP_IF, OP_NOTIF, OP_ELSE block
 */
public class OpEndIf implements FlowControl {
    @Override
    public OpCode opCode() {
        return new OpCode((short)0x68);
    }

    @Override
    public void execute(ScriptEnvironment env) {

    }
}
