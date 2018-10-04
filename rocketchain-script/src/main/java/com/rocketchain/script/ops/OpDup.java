package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;
import com.rocketchain.script.ScriptValue;
import com.rocketchain.utils.exception.ErrorCode;
import com.rocketchain.utils.exception.ScriptEvalException;

/** OP_DUP(0x76) : Duplicate the top item in the stack
 * Before : x
 * After  : x x
 */
public class OpDup implements StackOperation {
    @Override
    public OpCode opCode() {
        return new OpCode((short)0x76);
    }

    @Override
    public void execute(ScriptEnvironment env) {
        if (env.getStack().isEmpty()) {
            throw new ScriptEvalException(ErrorCode.NotEnoughInput, "ScriptOp:OpDup");
        }

        ScriptValue value = env.getStack().top();
        env.getStack().push(value.copy());
    }
}
