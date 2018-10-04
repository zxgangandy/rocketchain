package com.rocketchain.script;

import com.rocketchain.script.ops.ScriptOp;

import java.util.Arrays;
import java.util.List;

public class ScriptOpList {
    private List<ScriptOp> operations;

    public ScriptOpList(List<ScriptOp> operations) {
        this.operations = operations;
    }

    public List<ScriptOp> getOperations() {
        return operations;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(ScriptSerializer.serialize(operations));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (obj instanceof ScriptOpList) {
            if (this.operations.size() == ((ScriptOpList) obj).operations.size()) {
                //TODO: should opt
                return true;
//                return (this.operations.asSequence() zip other.operations.asSequence()).all { pair ->
//                        // BUGBUG : Should not call opCode for internal Ops such as OpCond.
//                        pair.first.opCode() == pair.second.opCode()

            } else {
                return false;
            }
        } else {
            return false;
        }

    }

}
