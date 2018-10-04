package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;
import com.rocketchain.script.ScriptValue;
import com.rocketchain.utils.exception.ErrorCode;
import com.rocketchain.utils.exception.ScriptEvalException;
import com.rocketchain.utils.lang.Utils;

/** OP_IFDUP(0x73) : Duplicate the top item in the stack if it is not 0
 * Before : x
 * After  : x     ( if x == 0 )
 * After  : x x   ( if x != 0 )
 */
public class OpIfDup implements StackOperation {
    @Override
    public OpCode opCode() {
        return new OpCode((short)0x73);
    }

    @Override
    public void execute(ScriptEnvironment env) {
        if (env.getStack().isEmpty()) {
            throw new ScriptEvalException(ErrorCode.NotEnoughInput, "ScriptOp:OpIfDup");
        }
        ScriptValue top = env.getStack().top();
        if (Utils.castToBool(top.getValue())) {
            env.getStack().push(top.copy());
        }
    }
}
