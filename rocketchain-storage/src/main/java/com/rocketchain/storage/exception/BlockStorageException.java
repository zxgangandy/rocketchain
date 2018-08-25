package com.rocketchain.storage.exception;

import com.rocketchain.utils.exception.ErrorCode;
import com.rocketchain.utils.exception.ExceptionWithErrorCode;

public class BlockStorageException extends ExceptionWithErrorCode {
    public BlockStorageException(ErrorCode code, String message) {
        super(code, message);
    }

    public BlockStorageException(ErrorCode code) {
        super(code);
    }
}
