package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;
import com.rocketchain.script.ScriptValue;
import com.rocketchain.utils.exception.ErrorCode;
import com.rocketchain.utils.exception.ScriptEvalException;

/** OP_FROMALTSTACK(0x6c) : Pop top item from alternative stack and push to stack
 * Before : (alt)x1
 * After  : x1
 */
public class OpFromAltStack implements StackOperation {
    @Override
    public OpCode opCode() {
        return new OpCode((short)0x6c);
    }

    @Override
    public void execute(ScriptEnvironment env) {
        if (env.getAltStack().size() < 1 ) {
            throw new ScriptEvalException(ErrorCode.NotEnoughInput, "ScriptOp:OpFromAltStack");
        }

        ScriptValue item = env.getAltStack().pop();
        // No need to copy, as we are moving the item.
        env.getStack().push(item);
    }
}
