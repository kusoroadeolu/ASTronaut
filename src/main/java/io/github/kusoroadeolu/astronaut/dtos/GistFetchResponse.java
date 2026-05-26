package io.github.kusoroadeolu.astronaut.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public record GistFetchResponse(Map<String, GistFileResponse> files) {

    public String content() {
        return files.values().stream().toList().getFirst().content();
    }

    public boolean isTruncated() {
        return files.values().stream().toList().getFirst().truncated();
    }

    public String rawUrl() {
        return files.values().stream().toList().getFirst().rawUrl();
    }

    public record GistFileResponse(String content, boolean truncated, @JsonProperty("raw_url") String rawUrl) {

    }
}
