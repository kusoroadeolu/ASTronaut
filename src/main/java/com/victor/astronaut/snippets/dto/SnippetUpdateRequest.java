package com.victor.astronaut.snippets.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.victor.astronaut.snippets.enums.SnippetLanguage;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.Set;

@Builder
public record SnippetUpdateRequest(
        @NotEmpty(message = "Snippet name cannot be empty")
        @NotNull(message = "Snippet name cannot be null")
        String snippetName,

        @JsonProperty(defaultValue = "OTHER")
        SnippetLanguage language,

        @NotNull(message = "Tags cannot be null")
        Set<String> tags,

        String content,
        String extraNotes
) {

    public SnippetUpdateRequest{
        content = content.trim();
        extraNotes = extraNotes.trim();
    }

}
