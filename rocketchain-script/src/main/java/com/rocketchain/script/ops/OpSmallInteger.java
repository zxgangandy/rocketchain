package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;

/** OP_SMALLINTEGER(0xfa) : Represents small integer data field
 * Node : This operation is not listed in the Bitcoin Script wiki, but in Mastering Bitcoin book.
 */
public class OpSmallInteger implements InternalScriptOp {
    @Override
    public OpCode opCode() {
        return new OpCode((short)0xfa);
    }

    @Override
    public void execute(ScriptEnvironment env) {
        assert(false);
    }
}
