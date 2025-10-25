package com.victor.astronaut.snippets.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.victor.astronaut.snippets.dto.utils.DtoUtils;
import com.victor.astronaut.snippets.enums.SnippetLanguage;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.Set;

import static com.victor.astronaut.snippets.dto.utils.DtoUtils.normalizeSet;

@Builder
public record SnippetUpdateRequest(
        @NotEmpty(message = "Snippet name cannot be empty")
        @NotNull(message = "Snippet name cannot be null")
        String snippetName,

        SnippetLanguage language,

        @NotNull(message = "Tags cannot be null")
        Set<String> tags,

        String content,
        String extraNotes
) {

    public SnippetUpdateRequest{
        language = language == null ? SnippetLanguage.OTHER : language;
        tags = normalizeSet(tags);
        content = content.trim();
        extraNotes = extraNotes.trim();
    }

}
