package com.rocketchain.utils.exception;

public class TransactionVerificationException extends ExceptionWithErrorCode {
    public TransactionVerificationException(ErrorCode code, String message) {
        super(code, message);
    }
}
