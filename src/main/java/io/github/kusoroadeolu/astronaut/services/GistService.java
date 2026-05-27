package io.github.kusoroadeolu.astronaut.services;

import io.github.kusoroadeolu.astronaut.dtos.*;
import io.github.kusoroadeolu.astronaut.exceptions.GistPersistenceException;
import io.github.kusoroadeolu.astronaut.exceptions.GithubAuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class GistService {
    private final RestClient client;

    public GistCreationResponse createGist(@NonNull GistCreationRequest request){
        return client.post()
                .uri("/gists")
                .body(request)
                .retrieve()
                .onStatus(status -> status.value() == 403, (_, res)  -> {
                    log.error("Failed to authorize user. Ensure you're using a valid PAT token. Err: {}", res.getStatusText());
                    throw new GithubAuthException("Failed to authorize user. Ensure you're using a valid PAT token. Err: %s".formatted(res.getStatusText()));
                })
                .onStatus(status -> status.value() == 422, (_, res) -> {
                    log.error("Failed to create gist due to malformed request body. Please try again. Err: {}", res.getStatusText());
                    throw new GistPersistenceException("Failed to create gist due to malformed request body. Please try again. Err: %s".formatted(res.getStatusText()));
                })
                .body(GistCreationResponse.class);
    }

    public void deleteGist(@NonNull String gistId){
        client.delete()
                .uri("/gists/%s".formatted(gistId))
                .retrieve()
                .onStatus(status -> status.value() == 403, (_, res)  -> {
                    log.error("Failed to authorize user. Ensure you're using a valid PAT token. Err: {}", res.getStatusText());
                    throw new GithubAuthException("Failed to authorize user. Ensure you're using a valid PAT token. Err: %s".formatted(res.getStatusText()));
                })
                .onStatus(status -> status.value() == 404, (_, res)  -> {})
                .toBodilessEntity();
    }

    public void updateGist(@NonNull String gistId, GistUpdateRequest request) {
        client.patch()
                .uri("/gists/%s".formatted(gistId))
                .body(request)
                .retrieve()
                .onStatus(status -> status.value() == 404, (_, res)  -> {
                    log.error("Failed to update gist due to the gist being non existent. Err: {}", res.getStatusText());
                    throw new GistPersistenceException("Failed to update gist due to the gist being non existent. Err: %s".formatted(res.getStatusText()));
                })
                .onStatus(status -> status.value() == 422, (_, res) -> {
                    log.error("Failed to update gist due to malformed request body. Please try again. Err: {}", res.getStatusText());
                    throw new GistPersistenceException("Failed to update gist due to malformed request body. Please try again. Err: %s".formatted(res.getStatusText()));
                })
                .toBodilessEntity();
    }

    public GistFetchResponse getGist(String gistId) {
        return client.get()
                .uri("/gists/%s".formatted(gistId))
                .retrieve()
                .onStatus(status -> status.value() == 404, (_, res)  -> {
                    log.error("Failed to get gist due to the gist being non existent. Err: {}", res.getStatusText());
                    throw new GistPersistenceException("Failed to get gist due to the gist being non existent. Err: %s".formatted(res.getStatusText()));
                })
                .onStatus(status -> status.value() == 403, (_, res) -> {
                    log.error("Failed to authorize user. Ensure you're using a valid PAT token. Err: {}", res.getStatusText());
                    throw new GithubAuthException("Failed to authorize user. Ensure you're using a valid PAT token. Err: %s".formatted(res.getStatusText()));
                })
                .body(GistFetchResponse.class);
    }

    public List<GistMultiFetchRequest> getAllGists() {
        var result = client.get()
                .uri("/gists")
                .retrieve()
                .onStatus(status -> status.value() == 403, (_, res) -> {
                    log.error("Failed to authorize user. Ensure you're using a valid PAT token. Err: {}", res.getStatusText());
                    throw new GithubAuthException("Failed to authorize user. Ensure you're using a valid PAT token. Err: %s".formatted(res.getStatusText()));
                })
                .body(GistMultiFetchRequest[].class);
        if (result == null || result.length == 0) return List.of();
        else return Arrays.stream(result).toList();
    }


}
