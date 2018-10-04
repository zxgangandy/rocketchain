package com.rocketchain.script;

import com.rocketchain.script.ops.ScriptOp;

public class ParseResult {
    private ScriptOpList scriptOpList;
    private ScriptOp foundFenceOp;
    private int bytesConsumed;

    /**
     * The parse result returned by parseUntil method.
     *
     * @param scriptOpList  The list of operations we got as a result of parsing a raw script.
     * @param foundFenceOp  The script operation found as a fence operation while parsing the script. null if no fence operation was found.
     * @param bytesConsumed The number of bytes consumed during parsing the script.
     */
    public ParseResult(ScriptOpList scriptOpList, ScriptOp foundFenceOp, int bytesConsumed) {
        this.scriptOpList = scriptOpList;
        this.foundFenceOp = foundFenceOp;
        this.bytesConsumed = bytesConsumed;
    }

    public ScriptOpList getScriptOpList() {
        return scriptOpList;
    }

    public ScriptOp getFoundFenceOp() {
        return foundFenceOp;
    }

    public int getBytesConsumed() {
        return bytesConsumed;
    }
}
