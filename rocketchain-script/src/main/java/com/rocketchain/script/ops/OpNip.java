package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;
import com.rocketchain.utils.exception.ErrorCode;
import com.rocketchain.utils.exception.ScriptEvalException;

/** OP_NIP(0x77) : Pop the second item in the stack
 * Before : x1 x2
 * After  : x2
 */
public class OpNip implements StackOperation {
    @Override
    public OpCode opCode() {
        return new OpCode((short)0x77);
    }

    @Override
    public void execute(ScriptEnvironment env) {
        if (env.getStack().size() < 2) {
            throw new ScriptEvalException(ErrorCode.NotEnoughInput, "ScriptOp:OpNip");
        }

        // Remove the second item on the stack.
        // The top item has index 0, so the second item in the stack has index 1.
        env.getStack().remove(1);
    }
}
