package io.github.kusoroadeolu.astronaut.dtos;

import java.util.Map;

public record GistFetchResponse(Map<String, GistFileResponse> files) {

    public String content() {
        return files.values().stream().toList().getFirst().content();
    }

    public record GistFileResponse(String content) {

    }
}
