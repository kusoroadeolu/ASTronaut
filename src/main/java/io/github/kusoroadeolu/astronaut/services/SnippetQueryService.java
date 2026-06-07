package io.github.kusoroadeolu.astronaut.services;

import io.github.kusoroadeolu.astronaut.SnippetCache;
import io.github.kusoroadeolu.astronaut.dtos.SearchCriteria;
import io.github.kusoroadeolu.astronaut.dtos.SnippetResponse;
import io.github.kusoroadeolu.astronaut.entities.SnippetIndex;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class SnippetQueryService {
    private final SnippetMapper snippetMapper;
    private final SnippetCache cache;

    @Value("${fuzzy-strength}")
    private int similarity;
    private static final LevenshteinDistance DISTANCE = LevenshteinDistance.getDefaultInstance();

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
        var ls = Arrays.stream(str.split(" ")).map(String::toLowerCase).toList();
        set.addAll(ls);
    }

    String replaceWithBlank(String original, String replace) {
       return original.replace(replace, "");
    }

    private boolean matchesLanguage(SnippetIndex s, SearchCriteria criteria) {
        if (criteria.getLanguages().isEmpty()) return false;
        return criteria.getLanguages()
                .stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet())
                .contains(s.getLanguage().toLowerCase());
    }

    private boolean matchesTags(SnippetIndex index, SearchCriteria criteria) {
        if (criteria.getTags().isEmpty()) return false;
        for (String s : index.getTags()){
            for (String ss : criteria.getTags()) {
                if (DISTANCE.apply(s, ss) <= similarity) return true;
            }
        }

        return false;
    }

    private boolean matchesFileNames(SnippetIndex s, SearchCriteria criteria) {
        if (criteria.getFileNames().isEmpty()) return false;
        for (String fileName : criteria.getFileNames()) {
            if (DISTANCE.apply(fileName, s.getFileName()) <= similarity) return true;
        }

        return false;
    }


    private boolean matchesMethodNames(SnippetIndex index, SearchCriteria criteria) {
        if (criteria.getMethodNames().isEmpty()) return false;
        for (String s : index.getMethodNames()){
            for (String ss : criteria.getMethodNames()) {
                if (DISTANCE.apply(s, ss) <= similarity) return true;
            }
        }

        return false;
    }

    private boolean matchesClassNames(SnippetIndex index, SearchCriteria criteria) {
        if (criteria.getClassNames().isEmpty()) return false;
        for (String s : index.getClassNames()){
            for (String ss : criteria.getClassNames()) {
                if (DISTANCE.apply(s, ss) <= similarity) return true;
            }
        }

        return false;
    }
}
