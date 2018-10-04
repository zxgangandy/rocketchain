package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;


public class OpNopN implements ReservedWords {
    private int value;

    /** OP_NOP1-OP_NOP10(0xb0-0xb9) : Does nothing, ignored. A data class for OP_NOP1 ~ OP_NOP10.
     * New opcodes can be added by means of a carefully designed and executed softfork using OP_NOP1 - OP_NOP10.
     *
     * @param value The number from 1 to 10.
     */
    public OpNopN(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public OpCode opCode() {
        return  opCodeFromBase(0xaf, value);
    }

    @Override
    public void execute(ScriptEnvironment env) {

    }
}
