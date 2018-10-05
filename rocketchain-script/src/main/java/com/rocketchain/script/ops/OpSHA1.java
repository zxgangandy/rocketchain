package com.rocketchain.script.ops;

import com.rocketchain.crypto.HashFunctions;
import com.rocketchain.crypto.SHA1;
import com.rocketchain.script.ScriptEnvironment;
import com.rocketchain.script.ScriptValue;
import com.rocketchain.utils.exception.ErrorCode;
import com.rocketchain.utils.exception.ScriptEvalException;

/** OP_SHA1(0xa7) : Return SHA1 hash of top item
 * Before : in
 * After  : hash
 */
public class OpSHA1 implements Crypto{
    @Override
    public OpCode opCode() {
        return new OpCode((short)0xa7);
    }

    @Override
    public void execute(ScriptEnvironment env) {
        if (env.getStack().isEmpty()) {
            throw new ScriptEvalException(ErrorCode.NotEnoughInput, "ScriptOp:OpSHA1");
        }
        ScriptValue topItem = env.getStack().pop();
        SHA1 hash = HashFunctions.sha1(topItem.getValue());
        env.getStack().push(ScriptValue.valueOf(hash.getValue().getArray()));
    }
}
