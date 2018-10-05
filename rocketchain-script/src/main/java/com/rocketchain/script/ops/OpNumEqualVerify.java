package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;
import com.rocketchain.utils.exception.ErrorCode;
import com.rocketchain.utils.exception.ScriptEvalException;


/**
 * OP_NUMEQUALVERIFY(0x9d) : Same as OP_NUMEQUAL, but runs OP_VERIFY afterward.
 */
public class OpNumEqualVerify implements Arithmetic {
    @Override
    public OpCode opCode() {
        return new OpCode((short) 0x9d);
    }

    @Override
    public void execute(ScriptEnvironment env) {
        binaryIntOperation(env, (l, r) -> (l == r) ? 1L : 0L);

        unaryIntOperation(env, (it) -> {
            if (it != 0L) {
                return it;
            } else
                throw new ScriptEvalException(ErrorCode.InvalidTransaction, "ScriptOp:OpNumEqualVerify");
        });
    }
}