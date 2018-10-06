package com.rocketchain.utils.exception;

public class UnsupportedFeature extends ExceptionWithErrorCode {
    public UnsupportedFeature(ErrorCode code) {
        super(code);
    }
}
