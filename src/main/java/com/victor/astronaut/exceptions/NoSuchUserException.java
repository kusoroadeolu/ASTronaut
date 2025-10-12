package com.victor.astronaut.exceptions;

public class NoSuchUserException extends RuntimeException {
    public NoSuchUserException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchUserException(String message) {
        super(message);
    }
}
