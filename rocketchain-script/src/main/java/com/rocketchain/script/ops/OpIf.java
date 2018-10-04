package com.rocketchain.script.ops;

import com.rocketchain.proto.Script;
import com.rocketchain.script.ScriptEnvironment;
import org.apache.commons.lang3.tuple.Pair;

/** OP_IF(0x63) : Execute the statements following if top of stack is not 0
 */
public class OpIf extends IfOrNotIfOp{
    @Override
    public OpCode opCode() {
        return new OpCode((short)0x63);
    }

    @Override
    public void execute(ScriptEnvironment env) {
        // never executed, because OP_IF ~ OP_ENDIF is converted to OpCond() during the parsing phase.
        assert(false);
    }

    @Override
    public Pair<ScriptOp, Integer> create(Script script, int offset) {
        return super.create(script, offset, false);
    }
}
