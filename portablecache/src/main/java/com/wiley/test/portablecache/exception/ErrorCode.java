package com.wiley.test.portablecache.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    CACHE_SIZE_ZERO(300,
            "In memory cache size is less than or equal to zero; which is not valid"),
    ERROR_CREATING_CACHE_META_FILE(301, "Error creating cache meta file"),
    CACHE_MAX_SIZE_REACHED(302, "Cache max size reached"),
    ERROR_READING_CACHE_FILES(303, "Error reading cache files"),
    ERROR_CREATING_CACHE_FILE(304, "Error creating cache file");

    private int id;
    private String description;

    private ErrorCode(int id, String description) {
        this.id = id;
        this.description = description;
    }
}
