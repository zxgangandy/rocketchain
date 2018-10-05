package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;

/** OP_NEGATE(0x8f) : Flip the sign of top item
 */
public class OpNegate implements Arithmetic {
    @Override
    public OpCode opCode() {
        return new OpCode((short)0x8f);
    }

    @Override
    public void execute(ScriptEnvironment env) {
        unaryIntOperation(env, (it)->- it );
    }
}
