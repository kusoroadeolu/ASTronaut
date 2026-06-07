package io.github.kusoroadeolu.astronaut.exceptions;

public class SnippetComparisonException extends RuntimeException {

    public SnippetComparisonException(String message, Throwable cause) {
        super(message, cause);
    }

    public SnippetComparisonException(String message) {
        super(message);
    }
}
