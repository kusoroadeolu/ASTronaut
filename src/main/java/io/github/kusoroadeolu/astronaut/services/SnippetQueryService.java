package io.github.kusoroadeolu.astronaut.services;

import io.github.kusoroadeolu.astronaut.SnippetCache;
import io.github.kusoroadeolu.astronaut.dtos.SearchCriteria;
import io.github.kusoroadeolu.astronaut.dtos.SnippetResponse;
import io.github.kusoroadeolu.astronaut.entities.SnippetIndex;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class SnippetQueryService {
    private final SnippetMapper snippetMapper;
    private final SnippetCache cache;


    public List<SnippetResponse> searchBasedOnCriteria(SearchCriteria criteria) {
        return cache.values().stream()
                .filter(s -> matchesLanguage(s, criteria))
                .filter(s -> matchesTagsOrNames(s, criteria))
                .filter(s -> matchesMethodNames(s, criteria))
                .filter(s -> matchesClassNames(s, criteria))
                .map(snippetMapper::toSnippetResponse)
                .toList();
    }

    private boolean matchesLanguage(SnippetIndex s, SearchCriteria criteria) {
        if (criteria.language().isBlank()) return true;
        return s.getLanguage().equalsIgnoreCase(criteria.language());
    }

    private boolean matchesTagsOrNames(SnippetIndex s, SearchCriteria criteria) {
        if (criteria.tagsOrNames().isEmpty()) return true;
        return !Collections.disjoint(s.getTags(), criteria.tagsOrNames())
                || criteria.tagsOrNames().contains(s.getFileName());
    }

    private boolean matchesMethodNames(SnippetIndex s, SearchCriteria criteria) {
        if (criteria.methodNames().isEmpty()) return true;
        return !Collections.disjoint(s.getMethodNames(), criteria.methodNames());
    }

    private boolean matchesClassNames(SnippetIndex s, SearchCriteria criteria) {
        if (criteria.classNames().isEmpty()) return true;
        return !Collections.disjoint(s.getClassNames(), criteria.classNames());
    }
}
