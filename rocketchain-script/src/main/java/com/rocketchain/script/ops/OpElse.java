package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;

/**  OP_ELSE(0x67) : Execute only if the previous statements were not executed
 */
public class OpElse implements FlowControl {
    @Override
    public OpCode opCode() {
        return new OpCode((short)0x67);
    }

    @Override
    public void execute(ScriptEnvironment env) {

    }
}
