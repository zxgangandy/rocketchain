package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;

public interface AlwaysInvalidScriptOp extends ScriptOp {

    /** Because we check if there is any *always* invalid script operation before executing the script,
     * the execute method should never run. So we implement this method to hit an assertion.
     *
     * @param env
     */
    @Override
    default void execute(ScriptEnvironment env) {
        assert(false);
    }
}
