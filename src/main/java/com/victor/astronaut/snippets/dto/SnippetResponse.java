package com.victor.astronaut.snippets.dto;

import java.time.LocalDateTime;

public record SnippetResponse(
        String name,
        String content,
        String extraNotes,
        boolean isDraft,
        LocalDateTime createdAt,
        LocalDateTime lastUpdated
) {
}
