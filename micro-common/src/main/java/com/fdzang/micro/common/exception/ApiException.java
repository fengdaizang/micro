package com.fdzang.micro.common.exception;

public class ApiException extends RuntimeException {
    private long code;

    public ApiException(long code, String message) {
        super(message);
        this.code = code;
    }

    public ApiException(long code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public long getCode() {
        return this.code;
    }
}
