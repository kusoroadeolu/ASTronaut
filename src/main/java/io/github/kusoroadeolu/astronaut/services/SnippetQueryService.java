package io.github.kusoroadeolu.astronaut.services;

import io.github.kusoroadeolu.astronaut.SnippetCache;
import io.github.kusoroadeolu.astronaut.dtos.SearchCriteria;
import io.github.kusoroadeolu.astronaut.dtos.SnippetResponse;
import io.github.kusoroadeolu.astronaut.entities.SnippetIndex;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class SnippetQueryService {
    private final SnippetMapper snippetMapper;
    private final SnippetCache cache;


    public List<SnippetResponse> searchBasedOnCriteria(String body) {
        SearchCriteria criteria = parseToCriteria(body);
        return cache.values().stream()
                .filter(s -> matchesLanguage(s, criteria)
                        || matchesTags(s, criteria)
                        || matchesMethodNames(s, criteria)
                        || matchesClassNames(s, criteria)
                        || matchesFileNames(s, criteria))
                .map(snippetMapper::toSnippetResponse)
                .toList();
    }

    /*
    * Sample syntax - tag: java concurrency; language: Java C++; name: Main Combiner and so on
    * Syntax as is - java concurrency, no keywords here, it just tries to match on all sets
    *
    * */
    SearchCriteria parseToCriteria(String body) {
        if (body == null || body.isBlank()) return SearchCriteria.EMPTY;
        List<String> split = Arrays.stream(body.split(";")).toList();
        SearchCriteria criteria = new SearchCriteria();
        for (String s : split) {
            if (s.startsWith("tag:")) {
                s = replaceWithBlank(s, "tag:");
                splitAndAdd(criteria.getTags(), s);
            } else if (s.startsWith("language:")) {
                s = replaceWithBlank(s, "language:");
                splitAndAdd(criteria.getLanguages(), s);
            } else if (s.startsWith("name:")) {
                s = replaceWithBlank(s, "name:");
                splitAndAdd(criteria.getFileNames(), s);
            } else if (s.startsWith("method-name:")) {
                s = replaceWithBlank(s, "method-name:");
                splitAndAdd(criteria.getMethodNames(), s);
            } else if (s.startsWith("class-name:")) {
                s = replaceWithBlank(s, "class-name:");
                splitAndAdd(criteria.getClassNames(), s);
            } else {
                criteria.addAll(Arrays.stream(s.split(" ")).toList());
            }
        }

        return criteria;
    }

    void splitAndAdd(Set<String> set, String str) {
        set.addAll(List.of(str.split(" ")));
    }

    String replaceWithBlank(String original, String replace) {
       return original.replace(replace, "");
    }

    private boolean matchesLanguage(SnippetIndex s, SearchCriteria criteria) {
        if (criteria.getLanguages().isEmpty()) return false;
        return criteria.getLanguages().contains(s.getLanguage());
    }

    private boolean matchesTags(SnippetIndex s, SearchCriteria criteria) {
        if (criteria.getTags().isEmpty()) return false;
        return !Collections.disjoint(s.getTags(), criteria.getTags());
    }

    private boolean matchesFileNames(SnippetIndex s, SearchCriteria criteria) {
        if (criteria.getFileNames().isEmpty()) return false;
        for (String fileName : criteria.getFileNames()) {
            if (s.getFileName().contains(fileName)) return true;
        }

        return false;
    }


    private boolean matchesMethodNames(SnippetIndex s, SearchCriteria criteria) {
        if (criteria.getMethodNames().isEmpty()) return false;
        return !Collections.disjoint(s.getMethodNames(), criteria.getMethodNames());
    }

    private boolean matchesClassNames(SnippetIndex s, SearchCriteria criteria) {
        if (criteria.getClassNames().isEmpty()) return false;
        return !Collections.disjoint(s.getClassNames(), criteria.getClassNames());
    }
}
