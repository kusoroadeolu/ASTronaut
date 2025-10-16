package com.victor.astronaut.snippets;

import com.victor.astronaut.snippets.dto.SnippetCreationRequest;
import com.victor.astronaut.snippets.dto.SnippetResponse;
import com.victor.astronaut.snippets.dto.SnippetUpdateRequest;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.transaction.annotation.Transactional;

public interface SnippetCrudService {
    @Transactional
    SnippetResponse createSnippet(long appUserId, @NonNull SnippetCreationRequest creationRequest);

    @Transactional
    void deleteSnippet(long appUserId, long snippetId);

    @Transactional
    SnippetResponse updateSnippet(long snippetId, long appUserId, @NonNull SnippetUpdateRequest updateRequest);

    @Transactional(readOnly = true)
    SnippetResponse findById(long appUserId, long snippetId);

    @Transactional(readOnly = true)
    Page<SnippetResponse> findSnippetsByUser(long appUserId,
                                             @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
                                             Pageable pageable);
}
