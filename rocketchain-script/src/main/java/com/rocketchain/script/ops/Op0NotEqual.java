package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;

/** OP_0NOTEQUAL(0x92) : Returns 0 if the input is 0. 1 otherwise.
 */
public class Op0NotEqual implements Arithmetic {
    @Override
    public OpCode opCode() {
        return new OpCode((short)0x92);
    }

    @Override
    public void execute(ScriptEnvironment env) {
        unaryIntOperation(env, (it)-> it == 0L ?  0L : 1L);
    }
}
