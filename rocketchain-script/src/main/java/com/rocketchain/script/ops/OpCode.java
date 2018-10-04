package com.rocketchain.script.ops;

public class OpCode {
    private Short code ;

    /** The OP code of an operation.
     *
     * @param code The OP code.
     */
    public OpCode(Short code) {
        this.code = code;
    }

    public Short getCode() {
        return code;
    }
}
