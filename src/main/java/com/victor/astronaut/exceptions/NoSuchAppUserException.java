package com.victor.astronaut.exceptions;

public class NoSuchAppUserException extends RuntimeException {
    public NoSuchAppUserException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchAppUserException(String message) {
        super(message);
    }
}
