package io.github.kusoroadeolu.astronaut.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.Map;

@Builder
public record GistCreationRequest(
        String description,
        @JsonProperty("public")
        boolean isPublic,
        Map<String, GistFileRequest> files
) {
    public record GistFileRequest(String content) {}
}