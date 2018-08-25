package com.rocketchain.utils.exception;

public class ChainException extends ExceptionWithErrorCode {
    public ChainException(ErrorCode code) {
        super(code);
    }

    public ChainException(ErrorCode code, String message) {
        super(code, message);
    }
}
