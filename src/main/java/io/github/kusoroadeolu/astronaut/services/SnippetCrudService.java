package io.github.kusoroadeolu.astronaut.services;

import io.github.kusoroadeolu.astronaut.SnippetCache;
import io.github.kusoroadeolu.astronaut.dtos.*;
import io.github.kusoroadeolu.astronaut.entities.SnippetIndex;
import io.github.kusoroadeolu.astronaut.exceptions.IndexPersistenceException;
import io.github.kusoroadeolu.astronaut.exceptions.NoSuchSnippetException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.StructuredTaskScope;

/**
 * Service implementation for managing code snippet CRUD operations.
 * Handles creating, reading, updating, and deleting snippets for users.
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class SnippetCrudService {

    private final SnippetMapper snippetMapper;
    private final GistMapper gistMapper;
    private final GistService gistService;
    private final SnippetCache cache;
    private final IndexFileService indexFileService;
    private final SnippetParsingService snippetParsingService;

    public SnippetResponse createSnippet(@NonNull SnippetCreationRequest request) {
        GistCreationRequest gistCreationRequest = gistMapper.fromSnippetCreationRequest(request);
        GistCreationResponse response = gistService.createGist(gistCreationRequest);
        SnippetIndex snippetIndex = snippetMapper.toSnippetIndex(request, response);
        snippetParsingService.parseSnippetContent(snippetIndex, request.content());
        cache.add(snippetIndex);
        indexFileService.writeToIndex();
        return snippetMapper.toSnippetResponse(snippetIndex);
    }

    public void deleteSnippet(String gistId) {
        boolean removed = cache.remove(gistId);
        if (removed) {
            gistService.deleteGist(gistId);
            indexFileService.writeToIndex();
        }
    }

    public SnippetResponse updateSnippet(String gistId, SnippetUpdateRequest updateRequest) {
        SnippetIndex snippetIndex = cache.get(gistId);
        if (snippetIndex == null) throw new NoSuchSnippetException("Failed to find a snippet with id: %s".formatted(gistId));
        log.info("Found snippet index: {}", snippetIndex);
        updateIfNecessary(gistId, snippetIndex, updateRequest);
        snippetIndex.setTags(updateRequest.tags());
        if (!updateRequest.description().isBlank()) snippetIndex.setDescription(updateRequest.description());
        indexFileService.writeToIndex();
        log.info("Updated snippet index: {}", snippetIndex);
        return snippetMapper.toSnippetResponse(snippetIndex);
    }

    void updateIfNecessary(String gistId, SnippetIndex snippetIndex,SnippetUpdateRequest updateRequest) {
        if (!updateRequest.content().equals(updateRequest.previousContent()) || !updateRequest.description().equals(snippetIndex.getDescription())){
            GistUpdateRequest gistUpdateRequest = gistMapper.fromSnippetUpdateRequest(snippetIndex.getFileName(), updateRequest);
            gistService.updateGist(gistId, gistUpdateRequest);
        }
    }

    public SnippetContentResponse findById(String gistId) {
        SnippetIndex snippetIndex = cache.get(gistId);
        if (snippetIndex == null) throw new NoSuchSnippetException("Failed to find a snippet with id: %s".formatted(gistId));
        GistFetchResponse fetchResponse = gistService.getGist(gistId);
        SnippetResponse snippetResponse = snippetMapper.toSnippetResponse(snippetIndex);
        return new SnippetContentResponse(snippetResponse, fetchResponse.content());
    }

    public List<SnippetResponse> getSnippets() {
       return cache.values().stream().map(snippetMapper::toSnippetResponse).toList();
    }

    public List<SnippetResponse> refreshFromGithub() {
        var results = gistService.getAllGists();
        Set<SnippetIndex> set = ConcurrentHashMap.newKeySet();
        try (var taskScope = StructuredTaskScope.open()) {
            for (int i = 0; i < results.size(); i++) {
                int j = i;
                taskScope.fork(() -> {
                   boolean isNew = false;
                   GistMultiFetchRequest request = results.get(j);
                   GistFetchResponse response = gistService.getGist(request.id());
                   SnippetIndex index = cache.get(request.id());
                   if (index == null) {
                       index = snippetMapper.fromMultiFetchRequest(request);
                       isNew = true;
                   }

                   snippetParsingService.parseSnippetContent(index, response.content());

                   if (isNew) set.add(index);
                });
            }

            taskScope.join();
        } catch (InterruptedException e) {
            throw new IndexPersistenceException("Failed to save some gists to index. Please try again", e);
        }

        cache.addAll(set);
        indexFileService.writeToIndex();
        return cache.values().stream()
                .map(snippetMapper::toSnippetResponse)
                .toList();
    }
}