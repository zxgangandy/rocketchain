package com.rocketchain.script;

import java.math.BigInteger;
import java.util.Arrays;

public class ScriptInteger extends ScriptValue {

    private BigInteger bigIntValue;

    public ScriptInteger(BigInteger bigIntValue) {
        this.bigIntValue = bigIntValue;
        value = encodeStackInt( bigIntValue );
    }

    @Override
    public ScriptValue copy() {
        return new ScriptInteger(bigIntValue);
    }

    @Override
    public int hashCode() {
        return  Arrays.hashCode(value);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null ) {
            return false;
        } else if(obj instanceof ScriptInteger ) {
            return ((ScriptInteger)obj).canEqual(this) && Arrays.equals(((ScriptInteger)obj).value, this.value);
        } else {
            return false;
        }
    }


    private boolean canEqual(Object a) {
        return a instanceof ScriptInteger;
    }
}
