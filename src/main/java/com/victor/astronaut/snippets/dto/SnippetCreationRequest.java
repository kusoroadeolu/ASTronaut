package com.victor.astronaut.snippets.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record SnippetCreationRequest(
    @NotEmpty(message = "Snippet name cannot be empty")
    @NotNull(message = "Snippet name cannot be null")
    String snippetName,
    Set<String> tags
) {
}
