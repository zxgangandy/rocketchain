package com.rocketchain.utils.exception;

public class WalletException extends ExceptionWithErrorCode {
    public WalletException(ErrorCode code) {
        super(code);
    }
}
