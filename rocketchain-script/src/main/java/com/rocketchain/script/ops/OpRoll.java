package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;
import com.rocketchain.script.ScriptValue;
import com.rocketchain.utils.exception.ErrorCode;
import com.rocketchain.utils.exception.ScriptEvalException;

/** OP_ROLL(0x7a) : Pop value N from top, then move the Nth item to the top of the stack
 * Before : xn ... x2 x1 x0 <n>
 * After  : ... x2 x1 x0 xn
 */
public class OpRoll implements StackOperation {
    @Override
    public OpCode opCode() {
        return new OpCode((short)0x7a);
    }

    @Override
    public void execute(ScriptEnvironment env) {
        if (env.getStack().size() < 2) {
            throw new ScriptEvalException(ErrorCode.NotEnoughInput, "ScriptOp:OpRoll");
        }

        // Get the <n> value
        int stackIndex = env.getStack().popInt().intValue();

        // Now, the stack should have at least stackIndex + 1 items.
        if (env.getStack().size() < stackIndex + 1) {
            throw new ScriptEvalException(ErrorCode.NotEnoughInput, "ScriptOp:OpRoll, Not enough input after getting the stack index");
        }
        ScriptValue itemAtIndex = env.getStack().remove(stackIndex);
        // No need to copy the item, as we are moving the item.
        env.getStack().push(itemAtIndex);
    }
}
