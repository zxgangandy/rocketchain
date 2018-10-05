package com.rocketchain.script.ops;

import com.rocketchain.crypto.HashFunctions;
import com.rocketchain.crypto.RIPEMD160;
import com.rocketchain.script.ScriptEnvironment;
import com.rocketchain.script.ScriptValue;
import com.rocketchain.utils.exception.ErrorCode;
import com.rocketchain.utils.exception.ScriptEvalException;

/**
 * OP_RIPEMD160(0xa6) : Return RIPEMD160 hash of top item
 * Before : in
 * After  : hash
 */
public class OpRIPEMD160 implements Crypto {
    @Override
    public OpCode opCode() {
        return new OpCode((short) 0xa6);
    }

    @Override
    public void execute(ScriptEnvironment env) {
        if (env.getStack().isEmpty()) {
            throw new ScriptEvalException(ErrorCode.NotEnoughInput, "ScriptOp:OpRIPEMD160");
        }

        ScriptValue topItem = env.getStack().pop();
        RIPEMD160 hash = HashFunctions.ripemd160(topItem.getValue());
        env.getStack().push(ScriptValue.valueOf(hash.getValue().getArray()));
    }
}
