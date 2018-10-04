package com.rocketchain.script;

import com.rocketchain.script.ops.ScriptOp;

public class ScriptInterpreter {
    /** Execute a parsed script. Return the value on top of the stack after the script execution.
     *
     * @param scriptOps A chunk of byte array after we get from ScriptParser.
     * @return the value on top of the stack after the script execution.
     */
    public static ScriptValue eval(ScriptOpList scriptOps )  {
        ScriptEnvironment env = new ScriptEnvironment();

        eval_internal(env, scriptOps);

        return env.getStack().pop();
    }

    /** Execute a list of script operations, but do not pop any item from the stack.
     * This method is called either by eval or ScriptOp.execute.
     *
     * Ex> OpCond may want to execute list of ScriptOp(s) on the then-statement-list.
     *
     * @param env The script execution environment.
     * @param scriptOps The list of script operations to execute.
     */
    public static void eval_internal(ScriptEnvironment env , ScriptOpList scriptOps ) {
        for (ScriptOp operation  : scriptOps.getOperations()) {
            operation.execute(env);
        }
    }
}
