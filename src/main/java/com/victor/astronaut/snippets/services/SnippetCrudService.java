package com.victor.astronaut.snippets.services;

import com.victor.astronaut.snippets.entities.Snippet;
import com.victor.astronaut.snippets.dtos.SnippetCreationRequest;
import com.victor.astronaut.snippets.dtos.SnippetResponse;
import com.victor.astronaut.snippets.dtos.SnippetUpdateRequest;
import com.victor.astronaut.snippets.projections.SnippetPreview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface SnippetCrudService {
    SnippetResponse createSnippet(long appUserId, SnippetCreationRequest creationRequest);

    void deleteSnippet(long appUserId, long snippetId);

    SnippetResponse updateSnippet(long snippetId, long appUserId, SnippetUpdateRequest updateRequest);

    SnippetResponse findById(long appUserId, long snippetId);

    Page<SnippetPreview> findSnippetsByUser(long appUserId,
                                            Pageable pageable);

    @Transactional(readOnly = true)
    Snippet findByAppUserIdAndId(long appUserId, long snippetId);
}
