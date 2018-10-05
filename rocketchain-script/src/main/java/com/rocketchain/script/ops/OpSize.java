package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;
import com.rocketchain.script.ScriptValue;
import com.rocketchain.utils.exception.ErrorCode;
import com.rocketchain.utils.exception.ScriptEvalException;

import java.math.BigInteger;

/**
 * OP_SIZE(0x82) : Calculate string length of top item and push the result
 * Before : in
 * After  : in size
 */
public class OpSize implements Splice {
    @Override
    public OpCode opCode() {
        return new OpCode((short) 0x82);
    }

    @Override
    public void execute(ScriptEnvironment env) {
        if (env.getStack().isEmpty()) {
            throw new ScriptEvalException(ErrorCode.NotEnoughInput, "ScriptOp:OpSize");
        }

        ScriptValue topValue = env.getStack().top();
        env.getStack().pushInt(BigInteger.valueOf(topValue.getValue().length));
    }
}
