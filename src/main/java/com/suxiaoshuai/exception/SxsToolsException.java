package com.suxiaoshuai.exception;

public class SxsToolsException extends RuntimeException {
    public SxsToolsException() {
    }

    public SxsToolsException(String message) {
        super(message);
    }

    public SxsToolsException(String message, Throwable cause) {
        super(message, cause);
    }

    public SxsToolsException(Throwable cause) {
        super(cause);
    }

    public SxsToolsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
