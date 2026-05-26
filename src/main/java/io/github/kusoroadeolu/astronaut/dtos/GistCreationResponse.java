package io.github.kusoroadeolu.astronaut.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.NonNull;

import java.util.Locale;
import java.util.Map;

public record GistCreationResponse(
        @NonNull
        String id,
        @JsonProperty("created_at")
        String createdAt,
        Map<String, GistFile> files
) {
    public record GistFile(
            String filename,
            String language
    ) {

        public GistFile {
            filename = filename.trim().toLowerCase();
            language = language.trim().toLowerCase();
        }

    }
}