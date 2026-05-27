package io.github.kusoroadeolu.astronaut.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public record GistMultiFetchRequest(
        String id,
        String description,
        Map<String, GistMultiFetchFile> files,
        @JsonProperty("created_at")
        String createdAt,
        @JsonProperty("updated_at")
        String updatedAt
) {
    public record GistMultiFetchFile(
            String filename,
            String language
    ) {

        public GistMultiFetchFile {
            filename = filename.trim();
            language = language.trim();
        }

    }
}
