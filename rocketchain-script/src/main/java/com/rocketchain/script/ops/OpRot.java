package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;
import com.rocketchain.script.ScriptValue;
import com.rocketchain.utils.exception.ErrorCode;
import com.rocketchain.utils.exception.ScriptEvalException;

/** OP_ROT(0x7b) : Rotate the top three items in the stack
 * Before : x1 x2 x3
 * After  : x2 x3 x1
 */
public class OpRot implements StackOperation {
    @Override
    public OpCode opCode() {
        return new OpCode((short)0x7b);
    }

    @Override
    public void execute(ScriptEnvironment env) {
        if (env.getStack().size() < 3) {
            throw new ScriptEvalException(ErrorCode.NotEnoughInput, "ScriptOp:OpRot");
        }

        ScriptValue thirdItem = env.getStack().remove(2);

        // No need to copy the item, as we are moving the item.
        env.getStack().push(thirdItem);
    }
}
