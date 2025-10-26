package com.victor.astronaut.snippets.dtos;

import com.victor.astronaut.snippets.enums.SnippetLanguage;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.Set;

import static com.victor.astronaut.snippets.dtos.utils.DtoUtils.*;

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
