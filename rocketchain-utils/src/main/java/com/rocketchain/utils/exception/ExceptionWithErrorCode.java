package com.rocketchain.utils.exception;

public class ExceptionWithErrorCode extends RuntimeException {
    protected ErrorCode code;

    public ExceptionWithErrorCode(ErrorCode code) {
        this(code.getDesc());
    }

    public ExceptionWithErrorCode(ErrorCode code, String message) {
        this(code.getDesc() + ": " + message);
    }

    public ExceptionWithErrorCode(String message) {
        super(message);
    }

    public ExceptionWithErrorCode(String message, Throwable cause) {
        super(message, cause);
    }

    public ExceptionWithErrorCode(Throwable cause) {
        super(cause);
    }

    public ErrorCode getCode() {
        return code;
    }
}
