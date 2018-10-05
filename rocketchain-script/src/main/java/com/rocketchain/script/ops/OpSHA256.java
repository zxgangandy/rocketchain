package com.rocketchain.script.ops;

import com.rocketchain.crypto.HashFunctions;
import com.rocketchain.crypto.SHA256;
import com.rocketchain.script.ScriptEnvironment;
import com.rocketchain.script.ScriptValue;
import com.rocketchain.utils.exception.ErrorCode;
import com.rocketchain.utils.exception.ScriptEvalException;

/** OP_SHA256(0xa8) : Return SHA256 hash of top item
 * Before : in
 * After  : hash
 */
public class OpSHA256 implements Crypto {
    @Override
    public OpCode opCode() {
        return new OpCode((short)0xa8);
    }

    @Override
    public void execute(ScriptEnvironment env) {
        if (env.getStack().isEmpty()) {
            throw new ScriptEvalException(ErrorCode.NotEnoughInput, "ScriptOp:OpSHA256");
        }
        ScriptValue topItem = env.getStack().pop();
        SHA256 hash = HashFunctions.sha256(topItem.getValue());
        env.getStack().push(ScriptValue.valueOf(hash.getValue().getArray()));
    }
}
