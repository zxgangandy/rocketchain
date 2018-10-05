package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;

/** OP_INVALIDOPCODE(0xff) : Represents any OP code not currently assigned
 */
public class OpInvalidOpCode implements InternalScriptOp {
    @Override
    public OpCode opCode() {
        return new OpCode((short)0xff);
    }

    @Override
    public void execute(ScriptEnvironment env) {
        assert(false);
    }
}
