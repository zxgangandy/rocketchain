package com.rocketchain.utils.exception;

public class GeneralException extends ExceptionWithErrorCode {
    public GeneralException(ErrorCode code) {
        super(code);
    }
}
