package com.victor.astronaut.exceptions;

public class RateLimitException extends RuntimeException {

    public RateLimitException(String message, Throwable cause) {
        super(message, cause);
    }

    public RateLimitException() {
        super();
    }

    public RateLimitException(String message) {
        super(message);
    }
}
