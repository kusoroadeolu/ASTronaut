package io.github.kusoroadeolu.astronaut.exceptions;

import java.time.LocalDateTime;

public record ApiError(
        int status,
        String message,
        LocalDateTime thrownAt
) {
}
