package com.bank.authorizer.domain.exception;

public class AlreadyExistsException extends RuntimeException {
    private final String message;

    public AlreadyExistsException(String message) {
        super(message);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
