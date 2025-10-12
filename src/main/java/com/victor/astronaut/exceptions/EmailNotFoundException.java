package com.victor.astronaut.exceptions;

public class EmailNotFoundException extends RuntimeException {

    public EmailNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailNotFoundException(String message) {
        super(message);
    }
}
