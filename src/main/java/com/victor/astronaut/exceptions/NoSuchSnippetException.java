package com.victor.astronaut.exceptions;

public class NoSuchSnippetException extends RuntimeException {
    public NoSuchSnippetException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchSnippetException(String message) {
        super(message);
    }
}
