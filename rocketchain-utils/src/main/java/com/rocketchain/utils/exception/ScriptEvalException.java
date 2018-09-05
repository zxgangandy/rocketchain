package com.rocketchain.utils.exception;

public class ScriptEvalException extends ExceptionWithErrorCode {
    public ScriptEvalException(ErrorCode code) {
        super(code);
    }

    public ScriptEvalException(ErrorCode code, String message) {
        super(code, message);
    }
}
