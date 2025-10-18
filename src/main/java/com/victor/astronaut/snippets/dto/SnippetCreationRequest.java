package com.victor.astronaut.snippets.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.victor.astronaut.snippets.SnippetLanguage;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.Set;

@Builder
public record SnippetCreationRequest(
    @NotBlank(message = "Snippet name cannot be blank")
    String snippetName,
    Set<String> tags,
    @JsonProperty(defaultValue = "OTHER")
    SnippetLanguage language
) {
}
