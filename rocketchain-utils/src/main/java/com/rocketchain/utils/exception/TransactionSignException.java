package com.rocketchain.utils.exception;

public class TransactionSignException extends ExceptionWithErrorCode {
    public TransactionSignException(ErrorCode code) {
        super(code);
    }

    public TransactionSignException(ErrorCode code, String message) {
        super(code, message);
    }

}
