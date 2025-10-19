package com.victor.astronaut.snippets.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.victor.astronaut.snippets.enums.SnippetLanguage;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.HashSet;
import java.util.Set;

@Builder
public record SnippetCreationRequest(
    @NotBlank(message = "Snippet name cannot be blank")
    String snippetName,
    Set<String> tags,
    @JsonProperty(defaultValue = "OTHER")
    SnippetLanguage language
) {
    public SnippetCreationRequest{
        tags = tags == null ? new HashSet<>() : tags;
    }

}
