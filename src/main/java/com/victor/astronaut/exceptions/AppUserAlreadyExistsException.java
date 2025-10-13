package com.victor.astronaut.exceptions;

public class AppUserAlreadyExistsException extends RuntimeException {
    public AppUserAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public AppUserAlreadyExistsException(String message) {
        super(message);
    }
}
