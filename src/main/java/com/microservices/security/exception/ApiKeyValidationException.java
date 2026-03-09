package com.microservices.security.exception;

public class ApiKeyValidationException extends RuntimeException {
    public ApiKeyValidationException(String message) {
        super(message);
    }
}

