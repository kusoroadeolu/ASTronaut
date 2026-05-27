package io.github.kusoroadeolu.astronaut.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.Set;

import static io.github.kusoroadeolu.astronaut.dtos.utils.DtoUtils.normalizeSet;

@Builder
public record SnippetCreationRequest(
    @NotBlank(message = "Snippet file name cannot be blank")
    String fileName,
    String description,
    @NotBlank(message = "Snippet content cannot be blank")
    String content,
    Set<String> tags
) {
    public SnippetCreationRequest{
        tags = normalizeSet(tags);
        description = description.trim();
    }

}
