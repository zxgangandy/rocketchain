package com.rocketchain.script.ops;

import com.rocketchain.proto.Script;
import com.rocketchain.script.ScriptEnvironment;
import org.apache.commons.lang3.tuple.Pair;

/** OP_NOTIF(0x64) : Execute the statements following if top of stack is 0
 */
public class OpNotIf extends IfOrNotIfOp {
    @Override
    public OpCode opCode() {
        return new OpCode((short)0x64);
    }

    @Override
    public void execute(ScriptEnvironment env) {
        // never executed, because OP_NOTIF ~ OP_ENDIF is converted to OpCond() during the parsing phase.
        assert(false);
    }

    @Override
    public Pair<ScriptOp, Integer> create(Script script, int offset) {
        // Call parse, check OP_ELSE, OP_ENDIF to produce thenStatementList, elseStatementList
        // Create OpCond with invert = false, thenStatementList, elseStatementList
        return super.create(script, offset, true);
    }
}
