package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;
import com.rocketchain.script.ScriptValue;
import com.rocketchain.utils.exception.ErrorCode;
import com.rocketchain.utils.exception.ScriptEvalException;

/** OP_2ROT(0x71) : Move the fifth and sixth items in the stack to the top
 * Before : x1 x2 x3 x4 x5 x6
 * After  : x3 x4 x5 x6 x1 x2
 */
public class Op2Rot implements StackOperation {
    @Override
    public OpCode opCode() {
        return new OpCode((short)0x71);
    }

    @Override
    public void execute(ScriptEnvironment env) {
        if (env.getStack().size() < 6) {
            throw new ScriptEvalException(ErrorCode.NotEnoughInput, "ScriptOp:Op2Rot");
        }

        ScriptValue fifthItem = env.getStack().remove(4);
        // instead of removing element at index 5, we have to remove an element at 4,
        // because the previous element at index 4 was removed.
        ScriptValue sixthItem = env.getStack().remove(4);

        env.getStack().push(sixthItem);
        env.getStack().push(fifthItem);
    }
}
