package com.victor.astronaut.snippets.dto;

import com.victor.astronaut.snippets.dto.utils.DtoUtils;
import com.victor.astronaut.snippets.enums.SnippetLanguage;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.victor.astronaut.snippets.dto.utils.DtoUtils.normalizeSet;

public record SearchCriteria(
        Set<SnippetLanguage> languages,
        Set<String> tagsOrNames,
        Set<String> classAnnotations,
        Set<String> classNames,
        Set<String> classFields,
        Set<String> classFieldAnnotations,
        Set<String> methodReturnTypes,
        Set<String> methodAnnotations
) {
    public SearchCriteria {
        languages = languages == null ? new HashSet<>() : languages;
        tagsOrNames = normalizeSet(tagsOrNames);
        classNames = normalizeSet(classNames);
        classAnnotations = normalizeSet(classAnnotations);
        classFields = normalizeSet(classFields);
        classFieldAnnotations =  normalizeSet(classFieldAnnotations);
        methodReturnTypes = normalizeSet(methodReturnTypes);
        methodAnnotations = normalizeSet(methodAnnotations);
    }


}