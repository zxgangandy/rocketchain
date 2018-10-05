package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;

/** OP_1ADD(0x8b) : Add 1 to the top item
 */
public class Op1Add implements Arithmetic{
    @Override
    public OpCode opCode() {
        return new OpCode((short)0x8b);
    }

    @Override
    public void execute(ScriptEnvironment env) {
        unaryIntOperation(env, (it) -> it + 1L);
    }
}
