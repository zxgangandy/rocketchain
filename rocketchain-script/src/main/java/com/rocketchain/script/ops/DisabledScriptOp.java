package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;
import com.rocketchain.utils.exception.ErrorCode;
import com.rocketchain.utils.exception.ScriptEvalException;

public interface DisabledScriptOp extends ScriptOp {
    @Override
    default void execute(ScriptEnvironment env) {
        throw new ScriptEvalException(ErrorCode.DisabledScriptOperation, "ScriptOp:${this.javaClass.getName()}") ;
    }
}
