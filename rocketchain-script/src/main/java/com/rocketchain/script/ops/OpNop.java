package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;

/** OP_NOP(0x61) : Do nothing. An operation for OP_NOP for a flow control.
 */
public class OpNop implements FlowControl {
    @Override
    public OpCode opCode() {
        return new  OpCode((short)0x61);
    }

    @Override
    public void execute(ScriptEnvironment env) {

    }
}
