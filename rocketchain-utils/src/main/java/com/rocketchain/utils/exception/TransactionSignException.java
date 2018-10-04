package com.rocketchain.utils.exception;

public class TransactionSignException extends ExceptionWithErrorCode {
    public TransactionSignException(ErrorCode code) {
        super(code);
    }
}
