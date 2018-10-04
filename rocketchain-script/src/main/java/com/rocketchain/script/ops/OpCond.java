package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;
import com.rocketchain.script.ScriptInterpreter;
import com.rocketchain.script.ScriptOpList;
import com.rocketchain.script.ScriptValue;
import com.rocketchain.utils.lang.Utils;

public class OpCond implements InternalScriptOp, ScriptOpWithoutCode {
    private boolean invert;
    private ScriptOpList thenStatementList;
    private ScriptOpList elseStatementList;

    /**
     * OpCond is a Pseudo Script Operation, which is created by the parser internally.
     * Users of the script language can't create it. So it does not have any OP code.
     * The class also is declared with ScriptOpWithoutCode, to allow the parser to check if it can get an OP code or not.
     * <p>
     * The following code snippet is a sample program with nested if statements.
     * <p>
     * 01 : <expression>
     * 02 : OP_IF                       -> (1) OpIf().create is called
     * -> (2) ScriptParser.parse is called.
     * 03 :   <expression>
     * 04 :   OP_IF                         -> (A) OpIf().create is called.
     * 05 :     then-statement-list         -> (B) ScriptParser.parse is called
     * 06 :   OP_ELSE
     * 07 :     else-statement-list
     * 08 :   OP_ENDIF
     * -> (3) OpIf().create returns OpCond with
     * then-statement-list(03-08) and
     * else-statement-list(10)
     * 09 : OP_ELSE
     * 10 :   then-statement-list
     * 11 : OP_ENDIF
     * <p>
     * The following list of statements are converted into one OpCond pseudo script operation.
     * <p>
     * OP_IF
     * then-statement-list
     * OP_ELSE
     * else-statement-list
     * OP_ENDIF
     * <p>
     * The OpCond script operation will have three fields.
     * <p>
     * 1. invert - true if the parser is producing OpCond while parsing OP_NOTIF.
     * 2. then-statement-list
     * 3. else-statement-list
     * <p>
     * Execution rule :
     * <p>
     * 1. POP the top item on the stack
     * case 1) invert is true
     * 2. run then-statement-list if the item is false.
     * 3. run else-statement-list part otherwise.
     * case 2) invert is false
     * 2. run then-statement-list if the item is true.
     * 3. run else-statement-list part otherwise.
     */
    public OpCond(boolean invert, ScriptOpList thenStatementList, ScriptOpList elseStatementList) {
        this.invert = invert;
        this.thenStatementList = thenStatementList;
        this.elseStatementList = elseStatementList;
    }

    public boolean isInvert() {
        return invert;
    }

    public ScriptOpList getThenStatementList() {
        return thenStatementList;
    }

    public ScriptOpList getElseStatementList() {
        return elseStatementList;
    }

    @Override
    public OpCode opCode() {
        return null;
    }

    @Override
    public void execute(ScriptEnvironment env) {
        ScriptValue top = env.getStack().top();
        boolean evaluatedValue = Utils.castToBool(top.getValue());
        if (invert) { // inverted.
            if (evaluatedValue) { //
                if (elseStatementList != null)
                    ScriptInterpreter.eval_internal(env, elseStatementList);
            } else {
                ScriptInterpreter.eval_internal(env, thenStatementList);
            }
        } else {
            if (evaluatedValue) {
                ScriptInterpreter.eval_internal(env, thenStatementList);
            } else {
                if (elseStatementList != null)
                    ScriptInterpreter.eval_internal(env, elseStatementList);
            }
        }
    }
}
