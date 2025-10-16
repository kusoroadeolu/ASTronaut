package com.victor.astronaut.exceptions;

public class SnippetPersistenceException extends RuntimeException {

    public SnippetPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public SnippetPersistenceException(String message) {
        super(message);
    }
}
