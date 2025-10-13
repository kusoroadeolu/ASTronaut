package com.victor.astronaut.exceptions;

public class AppUserPersistenceException extends RuntimeException {

    public AppUserPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public AppUserPersistenceException(String message) {
        super(message);
    }
}
