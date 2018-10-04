package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;
import com.rocketchain.script.ScriptValue;
import com.rocketchain.utils.exception.ErrorCode;
import com.rocketchain.utils.exception.ScriptEvalException;

/** OP_OVER(0x78) : Copy the second item in the stack and push it onto the top
 * Before : x1 x2
 * After  : x1 x2 x1
 */
public class OpOver implements StackOperation {
    @Override
    public OpCode opCode() {
        return new OpCode((short)0x78);
    }

    @Override
    public void execute(ScriptEnvironment env) {
        if (env.getStack().size() < 2) {
            throw new ScriptEvalException(ErrorCode.NotEnoughInput, "ScriptOp:OpOver");
        }

        ScriptValue secondItem = env.getStack().get(1);
        env.getStack().push( secondItem.copy() );
    }
}
