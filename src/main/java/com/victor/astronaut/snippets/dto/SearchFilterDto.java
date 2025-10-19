package com.victor.astronaut.snippets.dto;

import com.victor.astronaut.snippets.Snippet;
import com.victor.astronaut.snippets.SnippetLanguage;

import java.util.HashSet;
import java.util.Set;

public record SearchFilterDto(
        Set<SnippetLanguage> languages,
        Set<String> tagsOrNames,
        Set<String> classAnnotations,
        Set<String> classNames,
        Set<String> classFields,
        Set<String> classFieldAnnotations,
        Set<String> methodReturnTypes,
        Set<String> methodAnnotations
) {
    public SearchFilterDto {
        languages = languages == null ? new HashSet<>() : languages;
        tagsOrNames = tagsOrNames == null ? new HashSet<>() : tagsOrNames;
        classNames = classNames == null ? new HashSet<>() : classNames;
        classAnnotations = classAnnotations == null ? new HashSet<>() : classAnnotations;
        classFields = classFields == null ? new HashSet<>() : classFields;
        classFieldAnnotations = classFieldAnnotations == null ? new HashSet<>() : classFieldAnnotations;
        methodReturnTypes = methodReturnTypes == null ? new HashSet<>() : methodReturnTypes;
        methodAnnotations = methodAnnotations == null ? new HashSet<>() : methodAnnotations;
    }
}