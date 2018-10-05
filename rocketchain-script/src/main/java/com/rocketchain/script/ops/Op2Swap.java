package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;
import com.rocketchain.script.ScriptValue;
import com.rocketchain.utils.exception.ErrorCode;
import com.rocketchain.utils.exception.ScriptEvalException;

/** OP_2SWAP(0x72) : Swap the two top pairs of items in the stack
 * Before : x1 x2 x3 x4
 * After  : x3 x4 x1 x2
 */
public class Op2Swap implements StackOperation {
    @Override
    public OpCode opCode() {
        return new OpCode((short)0x72);
    }

    @Override
    public void execute(ScriptEnvironment env) {
        if (env.getStack().size() < 4) {
            throw new ScriptEvalException(ErrorCode.NotEnoughInput, "ScriptOp:Op2Swap");
        }

        ScriptValue thirdItem = env.getStack().remove(2);
        // instead of removing element at index 3, we have to remove an element at 2,
        // because the previous element at index 2 was removed.
        ScriptValue fourthItem = env.getStack().remove(2);

        env.getStack().push(fourthItem);
        env.getStack().push(thirdItem);
    }
}
