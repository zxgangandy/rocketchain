package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;
import com.rocketchain.utils.exception.ErrorCode;
import com.rocketchain.utils.exception.ScriptEvalException;

/** OP_DROP(0x75) : Pop the top item in the stack
 * Before : x
 * After  :
 */
public class OpDrop implements StackOperation {
    @Override
    public OpCode opCode() {
        return new OpCode((short)0x75);
    }

    @Override
    public void execute(ScriptEnvironment env) {
        if (env.getStack().isEmpty()) {
            throw new ScriptEvalException(ErrorCode.NotEnoughInput, "ScriptOp:OpDrop");
        }

        env.getStack().pop();
    }
}
