package io.github.kusoroadeolu.astronaut.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.Set;

import static io.github.kusoroadeolu.astronaut.dtos.utils.DtoUtils.normalizeSet;

@Builder
public record SnippetUpdateRequest(
        @NotBlank(message = "Description cannot be empty")
        String description,

        @NotNull(message = "Tags cannot be null")
        Set<String> tags,
        @NotNull(message = "Previous Tags cannot be null")
        Set<String> previousTags,

        @NotBlank(message = "Snippet content cannot be blank")
        String content,


        @NotBlank(message = "Previous snippet content cannot be blank")
        String previousContent
) {

    public SnippetUpdateRequest{
        tags = normalizeSet(tags);
        content = content.trim();
        description = description.trim();
    }

}
