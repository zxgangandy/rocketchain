package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;

import java.math.BigInteger;

/**
 * OP_DEPTH(0x74) : Count the items on the stack and push the resulting count
 * Before :
 * After  : <stack size>
 */
public class OpDepth implements StackOperation {
    @Override
    public OpCode opCode() {
        return new OpCode((short) 0x74);
    }

    @Override
    public void execute(ScriptEnvironment env) {
        int stackSize = env.getStack().size();
        env.getStack().pushInt(BigInteger.valueOf(stackSize));
    }
}
