package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;
import com.rocketchain.script.ScriptValue;

import java.util.Arrays;

/**
 * OP_EQUALVERIFY(0x88) : Same as OP_EQUAL, but run OP_VERIFY after to halt if not TRUE
 * Before : x1 x2
 * After :
 */
public class OpEqualVerify implements BitwiseLogic {
    @Override
    public OpCode opCode() {
        return new OpCode((short) 0x88);
    }

    @Override
    public void execute(ScriptEnvironment env) {
        binaryOperation(env, (l, r) ->
                Arrays.equals(l.getValue(), r.getValue()) ? ScriptValue.valueOf(1L) : ScriptValue.valueOf(""));
        verify(env);
    }
}
