package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;
import com.rocketchain.script.ScriptValue;
import com.rocketchain.utils.exception.ErrorCode;
import com.rocketchain.utils.exception.ScriptEvalException;

/** OP_SWAP(0x7c) : Swap the top three items in the stack
 * Before : x1 x2
 * After  : x2 x1
 */
public class OpSwap implements StackOperation {
    @Override
    public OpCode opCode() {
        return new OpCode((short)0x7c);
    }

    @Override
    public void execute(ScriptEnvironment env) {
        if (env.getStack().size() < 2) {
            throw new ScriptEvalException(ErrorCode.NotEnoughInput, "ScriptOp:OpSwap");
        }

        ScriptValue secondItem = env.getStack().remove(1);

        // No need to copy the item, as we are moving the item.
        env.getStack().push(secondItem);
    }
}
