package com.bank.baas.domain.exception;

public class AuthorizationException extends RuntimeException{
    private final String message;

    public AuthorizationException(String message) {
        super(message);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
