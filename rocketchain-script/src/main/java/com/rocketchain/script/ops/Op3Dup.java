package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;
import com.rocketchain.script.ScriptValue;
import com.rocketchain.utils.exception.ErrorCode;
import com.rocketchain.utils.exception.ScriptEvalException;

/** OP_3DUP(0x6f) : Duplicate top three stack items
 * Before : x1 x2 x3
 * After  : x1 x2 x3 x1 x2 x3
 */
public class Op3Dup implements StackOperation {
    @Override
    public OpCode opCode() {
        return new OpCode((short)0x6f);
    }

    @Override
    public void execute(ScriptEnvironment env) {
        if (env.getStack().size() < 3) {
            throw new ScriptEvalException(ErrorCode.NotEnoughInput, "ScriptOp:Op3Dup");
        }
        ScriptValue topItem = env.getStack().get(0);
        ScriptValue secondItem = env.getStack().get(1);
        ScriptValue thirdItem = env.getStack().get(2);

        env.getStack().push(thirdItem.copy());
        env.getStack().push(secondItem.copy());
        env.getStack().push(topItem.copy());
    }
}
