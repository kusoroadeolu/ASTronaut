package io.github.kusoroadeolu.astronaut.exceptions;

public class SnippetParseException extends RuntimeException {
    public SnippetParseException(String message) {
        super(message);
    }

    public SnippetParseException() {
        super();
    }
}
