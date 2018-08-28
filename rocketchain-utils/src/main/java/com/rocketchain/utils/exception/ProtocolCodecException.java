package com.rocketchain.utils.exception;

public class ProtocolCodecException extends ExceptionWithErrorCode {
    public ProtocolCodecException(ErrorCode code) {
        super(code);
    }

    public ProtocolCodecException(ErrorCode code, String message) {
        super(code, message);
    }
}
