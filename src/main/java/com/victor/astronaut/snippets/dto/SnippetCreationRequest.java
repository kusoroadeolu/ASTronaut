package com.victor.astronaut.snippets.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.victor.astronaut.snippets.dto.utils.DtoUtils;
import com.victor.astronaut.snippets.enums.SnippetLanguage;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.HashSet;
import java.util.Set;

import static com.victor.astronaut.snippets.dto.utils.DtoUtils.*;

@Builder
public record SnippetCreationRequest(
    @NotBlank(message = "Snippet name cannot be blank")
    String snippetName,
    Set<String> tags,
    SnippetLanguage language
) {
    public SnippetCreationRequest{
        tags = normalizeSet(tags);
        snippetName = snippetName.trim();
        language = language == null ? SnippetLanguage.OTHER : language;

    }

}
