package com.victor.astronaut.snippets;

import com.victor.astronaut.snippets.dto.SnippetCreationRequest;
import com.victor.astronaut.snippets.dto.SnippetResponse;
import com.victor.astronaut.snippets.dto.SnippetUpdateRequest;
import com.victor.astronaut.snippets.projections.SnippetPreview;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
