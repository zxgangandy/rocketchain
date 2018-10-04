package com.rocketchain.script.ops;

import com.rocketchain.proto.Script;
import com.rocketchain.script.ParseResult;
import com.rocketchain.script.ScriptEnvironment;
import com.rocketchain.script.ScriptOpList;
import com.rocketchain.script.ScriptParser;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class IfOrNotIfOp implements FlowControl {
    @Override
    public OpCode opCode() {
        return null;
    }

    @Override
    public void execute(ScriptEnvironment env) {

    }

    @Override
    public Pair<ScriptOp, Integer> create(Script script, int offset) {
        return null;
    }


    public Pair<ScriptOp, Integer> create(Script script, int offset, boolean invert) {
        // Call parse, check OP_ELSE, OP_ENDIF to produce thenStatementList, elseStatementList
        // Create OpCond with invert = false, thenStatementList, elseStatementList

        ParseResult thenPart  =
                ScriptParser.parseUntil(script, offset, new OpElse(), new OpEndIf());

        // TODO : Implement equals method for ScriptOp.
        Pair<ScriptOpList, Integer> pair;
        if ( thenPart.getFoundFenceOp().opCode() == new OpElse().opCode()) {
            ParseResult elsePart = ScriptParser.parseUntil(script, offset + thenPart.getBytesConsumed(), new OpEndIf());
            assert(elsePart.getFoundFenceOp().opCode() == new OpEndIf().opCode());
            pair = new MutablePair(elsePart.getScriptOpList(), elsePart.getBytesConsumed());
        } else {
            assert( thenPart.getFoundFenceOp().opCode() == new OpEndIf().opCode() );
            pair = new MutablePair<>(null, 0);
        }

        ScriptOpList elseScriptOpList = pair.getLeft();
        int elsePartBytesConsumed = pair.getRight();
        return new MutablePair <ScriptOp, Integer>( new OpCond(invert, thenPart.getScriptOpList(), elseScriptOpList ),
                thenPart.getBytesConsumed() + elsePartBytesConsumed );
    }
}
