package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;
import com.rocketchain.script.ScriptValue;
import com.rocketchain.utils.exception.ErrorCode;
import com.rocketchain.utils.exception.ScriptEvalException;

/** OP_TOALTSTACK(0x6b) : Pop top item from stack and push to alternative stack
 * Before : x1
 * After  : (alt)x1
 */
public class OpToAltStack implements StackOperation{
    @Override
    public OpCode opCode() {
        return new OpCode((short)0x6b);
    }

    @Override
    public void execute(ScriptEnvironment env) {
        if (env.getStack().isEmpty() ) {
            throw new ScriptEvalException(ErrorCode.NotEnoughInput, "ScriptOp:OpToAltStack");
        }

        ScriptValue item = env.getStack().pop();
        // No need to copy, as we are moving the item.
        env.getAltStack().push(item);
    }
}
