package com.landregistry.exception;

public class LandParcelNotFoundException extends RuntimeException {
    public LandParcelNotFoundException(String message) {
        super(message);
    }
}
