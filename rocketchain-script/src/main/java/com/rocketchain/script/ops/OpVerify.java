package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;

/** OP_VERIFY(0x69) : Check the top of the stack, halt and invalidate transaction if not TRUE
 */
public class OpVerify implements FlowControl {
    @Override
    public OpCode opCode() {
        return new OpCode((short)0x69);
    }

    @Override
    public void execute(ScriptEnvironment env) {
        verify(env);
    }
}
