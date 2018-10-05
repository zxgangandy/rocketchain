package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;
import com.rocketchain.script.ScriptValue;
import com.rocketchain.utils.exception.ErrorCode;
import com.rocketchain.utils.exception.ScriptEvalException;

/** OP_TUCK(0x7d) : The item at the top of the stack is copied and inserted before the second-to-top item.
 * Before : s x1 x2
 * After  : s x2 x1 x2
 */
public class OpTuck implements StackOperation {
    @Override
    public OpCode opCode() {
        return new OpCode((short)0x7d);
    }

    @Override
    public void execute(ScriptEnvironment env) {
        if (env.getStack().size() < 2) {
            throw new ScriptEvalException(ErrorCode.NotEnoughInput, "ScriptOp:OpTuck");
        }

        ScriptValue topItem = env.getStack().top();
        // Insert the element at the given position.
        env.getStack().insert(1, topItem.copy());
    }
}
