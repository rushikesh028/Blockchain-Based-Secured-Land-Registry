package com.landregistry.exception;

public class DuplicateParcelException extends RuntimeException {
    public DuplicateParcelException(String message) {
        super(message);
    }
}
