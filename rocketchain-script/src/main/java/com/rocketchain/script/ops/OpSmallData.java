package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;

/** OP_SMALLDATA(0xf9) : Represents small data field
 * Node : This operation is not listed in the Bitcoin Script wiki, but in Mastering Bitcoin book.
 */
public class OpSmallData implements InternalScriptOp {
    @Override
    public OpCode opCode() {
        return new OpCode((short)0xf9);
    }

    @Override
    public void execute(ScriptEnvironment env) {
        assert(false);
    }
}
