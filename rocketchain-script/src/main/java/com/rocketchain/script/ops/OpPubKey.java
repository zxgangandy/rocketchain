package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;

/** OP_PUBKEY(0xfe) : Represents a public key field
 */
public class OpPubKey implements InternalScriptOp{
    @Override
    public OpCode opCode() {
        return new OpCode((short)0xfe);
    }

    @Override
    public void execute(ScriptEnvironment env) {
        assert(false);
    }
}
