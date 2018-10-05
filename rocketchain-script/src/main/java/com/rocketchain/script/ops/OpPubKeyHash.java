package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;

/** OP_PUBKEYHASH(0xfd) : Represents a public key hash field
 */
public class OpPubKeyHash implements InternalScriptOp {
    @Override
    public OpCode opCode() {
        return new OpCode((short)0xfd);
    }

    @Override
    public void execute(ScriptEnvironment env) {
        assert(false);
    }
}
