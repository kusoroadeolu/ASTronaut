package io.github.kusoroadeolu.astronaut.services;

import io.github.kusoroadeolu.astronaut.dtos.GistCreationRequest;
import io.github.kusoroadeolu.astronaut.dtos.GistUpdateRequest;
import io.github.kusoroadeolu.astronaut.dtos.SnippetCreationRequest;
import io.github.kusoroadeolu.astronaut.dtos.SnippetUpdateRequest;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GistMapper {
    public GistCreationRequest fromSnippetCreationRequest(SnippetCreationRequest request) {
        return GistCreationRequest
                .builder()
                .description(request.description())
                .files(Map.of(
                        request.fileName(), new GistCreationRequest.GistFileRequest(request.content())
                ))
                .isPublic(true)
                .build();
    }

    public GistUpdateRequest fromSnippetUpdateRequest(String fileName, SnippetUpdateRequest updateRequest) {
        return GistUpdateRequest
                .builder()
                .description(updateRequest.description())
                .files(Map.of(fileName, new GistCreationRequest.GistFileRequest(updateRequest.content())))
                .build();
    }
}
