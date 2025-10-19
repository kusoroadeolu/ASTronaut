package com.victor.astronaut.snippets;

import com.victor.astronaut.snippets.dto.SearchCriteria;
import com.victor.astronaut.snippets.projections.SnippetPreview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SnippetQueryService {
    Page<SnippetPreview> searchBasedOnCriteria(long id, SearchCriteria filterDto, Pageable pageable);
}
