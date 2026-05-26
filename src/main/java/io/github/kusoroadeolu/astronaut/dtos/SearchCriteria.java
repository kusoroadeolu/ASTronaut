package io.github.kusoroadeolu.astronaut.dtos;


import org.jspecify.annotations.NonNull;

import java.util.Set;

import static io.github.kusoroadeolu.astronaut.dtos.utils.DtoUtils.normalizeSet;

public record SearchCriteria(
        @NonNull
        String language,
        Set<String> tagsOrNames,
        Set<String> methodNames,
        Set<String> classNames

) {
    public SearchCriteria {
        tagsOrNames = normalizeSet(tagsOrNames);
        classNames = normalizeSet(classNames);
        methodNames = normalizeSet(methodNames);
    }


}