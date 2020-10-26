package com.wiley.test.portablecache.exception;

import lombok.Getter;

@Getter
public class CacheException extends RuntimeException {

    private ErrorCode errorCode;

    public CacheException(Exception exception, ErrorCode errorCode) {
        super(errorCode.getDescription(), exception);
        this.errorCode = errorCode;
    }

    public CacheException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
    }
}
