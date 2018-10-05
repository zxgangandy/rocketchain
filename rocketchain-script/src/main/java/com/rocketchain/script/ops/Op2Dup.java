package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;
import com.rocketchain.script.ScriptValue;
import com.rocketchain.utils.exception.ErrorCode;
import com.rocketchain.utils.exception.ScriptEvalException;

/** OP_2DUP(0x6e) : Duplicate top two stack items
 * Before : x1 x2
 * After  : x1 x2 x1 x2
 */
public class Op2Dup implements StackOperation {
    @Override
    public OpCode opCode() {
        return new OpCode((short)0x6e);
    }

    @Override
    public void execute(ScriptEnvironment env) {
        if (env.getStack().size() < 2) {
            throw new ScriptEvalException(ErrorCode.NotEnoughInput, "ScriptOp:Op2Dup");
        }
        ScriptValue topItem = env.getStack().get(0);
        ScriptValue secondItem = env.getStack().get(1);

        env.getStack().push(secondItem.copy());
        env.getStack().push(topItem.copy());
    }
}
