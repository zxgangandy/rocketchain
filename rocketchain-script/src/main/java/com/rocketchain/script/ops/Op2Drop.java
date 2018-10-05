package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;
import com.rocketchain.utils.exception.ErrorCode;
import com.rocketchain.utils.exception.ScriptEvalException;

/** OP_2DROP(0x6d) : Pop top two stack items
 * Before : x1 x2
 * After  :
 */
public class Op2Drop implements StackOperation {
    @Override
    public OpCode opCode() {
        return new OpCode((short)0x6d);
    }

    @Override
    public void execute(ScriptEnvironment env) {
        if (env.getStack().size() < 2) {
            throw new ScriptEvalException(ErrorCode.NotEnoughInput, "ScriptOp:Op2Drop");
        }

        env.getStack().pop();
        env.getStack().pop();
    }
}
