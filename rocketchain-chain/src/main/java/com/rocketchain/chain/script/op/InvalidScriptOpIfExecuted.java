package com.rocketchain.chain.script.op;

import com.rocketchain.chain.script.ScriptEnvironment;
import com.rocketchain.utils.exception.ErrorCode;
import com.rocketchain.utils.exception.ScriptEvalException;

public interface InvalidScriptOpIfExecuted extends ScriptOp {

    @Override
    default void execute(ScriptEnvironment env) {
        throw new ScriptEvalException(ErrorCode.InvalidTransaction, "ScriptOp:{" + getClass().getName() + "}");
    }
}
