package com.victor.astronaut.snippets.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
public record SnippetResponse(
        long id,
        Set<String> tags,
        @NotBlank(message = "Snippet name is required")
        String name,
        String content,
        String extraNotes,
        boolean isDraft,
        String language,
        LocalDateTime createdAt,
        LocalDateTime lastUpdated
) {
}
