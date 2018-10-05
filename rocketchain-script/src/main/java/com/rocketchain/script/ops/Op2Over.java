package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;
import com.rocketchain.script.ScriptValue;
import com.rocketchain.utils.exception.ErrorCode;
import com.rocketchain.utils.exception.ScriptEvalException;

/** OP_2OVER(0x70) : Copy the third and fourth items in the stack to the top
 * Before : x1 x2 x3 x4
 * After  : x1 x2 x3 x4 x1 x2
 */
public class Op2Over implements StackOperation {
    @Override
    public OpCode opCode() {
        return new OpCode((short)0x70);
    }

    @Override
    public void execute(ScriptEnvironment env) {
        if (env.getStack().size() < 4) {
            throw new ScriptEvalException(ErrorCode.NotEnoughInput, "ScriptOp:Op2Over");
        }

        ScriptValue thirdItem = env.getStack().get(2);
        ScriptValue fourthItem = env.getStack().get(3);

        env.getStack().push(fourthItem);
        env.getStack().push(thirdItem);
    }
}
