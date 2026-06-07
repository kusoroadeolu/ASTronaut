package io.github.kusoroadeolu.astronaut.dtos;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
public record SnippetResponse(
        String id,
        String name,
        String language,
        String description,
        Set<String>tags,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
