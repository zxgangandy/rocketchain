package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;
import com.rocketchain.script.ScriptValue;

import java.util.Arrays;

/**
 * OP_EQUAL(0x87) : Push TRUE (1) if top two items are exactly equal, push FALSE (0) otherwise
 */
public class OpEqual implements BitwiseLogic {
    @Override
    public OpCode opCode() {
        return new OpCode((short) 0x87);
    }

    @Override
    public void execute(ScriptEnvironment env) {
        binaryOperation(env, (l, r) ->
                Arrays.equals(l.getValue(), r.getValue()) ? ScriptValue.valueOf(1L) : ScriptValue.valueOf(0L)
        );
    }
}
