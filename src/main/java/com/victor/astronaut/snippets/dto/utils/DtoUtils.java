package com.victor.astronaut.snippets.dto.utils;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class DtoUtils {

    public static Set<String> normalizeSet(Set<String> set){
        if(set != null)
          set.forEach(e -> System.out.println("Passed value: " + set));

        return set == null ? new HashSet<>() : set.stream()
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }

}
