package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;
import com.rocketchain.script.ScriptValue;
import com.rocketchain.utils.exception.ErrorCode;
import com.rocketchain.utils.exception.ScriptEvalException;

/** OP_PICK(0x79) : Pop value N from top, then copy the Nth item to the top of the stack
 * Before : xn ... x2 x1 x0 <n>
 * After  : xn ... x2 x1 x0 xn
 */
public class OpPick implements StackOperation {
    @Override
    public OpCode opCode() {
        return new OpCode((short)0x79);
    }

    @Override
    public void execute(ScriptEnvironment env) {
        if (env.getStack().size() < 2) {
            throw new ScriptEvalException(ErrorCode.NotEnoughInput, "ScriptOp:OpPick");
        }

        // Get the <n> value
        int stackIndex = env.getStack().popInt().intValue();

        // Now, the stack should have at least stackIndex + 1 items.
        if (env.getStack().size() < stackIndex + 1) {
            throw new ScriptEvalException(ErrorCode.NotEnoughInput, "ScriptOp:OpPick, Not enough input after getting the stack index");
        }
        ScriptValue itemAtIndex = env.getStack().get(stackIndex);
        env.getStack().push(itemAtIndex.copy());
    }
}
