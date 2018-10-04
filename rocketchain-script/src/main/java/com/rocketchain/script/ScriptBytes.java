package com.rocketchain.script;

import java.util.Arrays;

public class ScriptBytes extends ScriptValue {

    public ScriptBytes(byte[] bytesValue) {
        value = bytesValue;
    }


    @Override
    public ScriptValue copy() {
        return new ScriptBytes(value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }


    @Override
    public boolean equals(Object obj) {

        if (obj == null) {
            return false;
        } else if (obj instanceof ScriptBytes) {
            return ((ScriptBytes) obj).canEqual(this) && Arrays.equals(((ScriptBytes) obj).value, value);
        } else {
            return false;
        }
    }

    boolean canEqual(Object obj) {
        return obj instanceof ScriptBytes;
    }
}
