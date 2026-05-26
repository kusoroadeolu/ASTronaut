package io.github.kusoroadeolu.astronaut.dtos;

import io.github.kusoroadeolu.astronaut.dtos.GistCreationRequest.GistFileRequest;
import lombok.Builder;

import java.util.Map;

@Builder
public record GistUpdateRequest(String description, Map<String, GistFileRequest> files) {
}
