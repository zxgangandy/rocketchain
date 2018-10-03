package com.rocketchain.chain.script.ops;


import com.rocketchain.chain.script.ScriptEnvironment;
import com.rocketchain.chain.script.ScriptValue;
import com.rocketchain.chain.script.op.OpCode;
import com.rocketchain.crypto.Hash160;
import com.rocketchain.crypto.HashFunctions;
import com.rocketchain.proto.Hash;
import com.rocketchain.utils.exception.ErrorCode;
import com.rocketchain.utils.exception.ScriptEvalException;

/** OP_HASH160(0xa9) : Return RIPEMD160(SHA256(x)) hash of top item
 * Before : in
 * After  : hash
 */
public class OpHash160 implements Crypto {
    @Override
    public OpCode opCode() {
        return new OpCode((short)0xa9);
    }

    @Override
    public void execute(ScriptEnvironment env) {
        if (env.getStack().isEmpty()) {
            throw new ScriptEvalException(ErrorCode.NotEnoughInput, "ScriptOp:OpHash160");
        }
        ScriptValue topItem = env.getStack().pop();
        Hash160 hash =  HashFunctions.hash160(topItem.getValue());
        env.getStack().push(ScriptValue.valueOf(hash.getValue().getArray()));
    }
}
