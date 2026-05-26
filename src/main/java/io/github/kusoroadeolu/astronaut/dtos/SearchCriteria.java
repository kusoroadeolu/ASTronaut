package io.github.kusoroadeolu.astronaut.dtos;


import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.github.kusoroadeolu.astronaut.dtos.utils.DtoUtils.normalizeSet;

@Getter
@Setter
public class SearchCriteria {

    public static final SearchCriteria EMPTY = new SearchCriteria();

    private Set<String> languages;
    private final Set<String> tags;
    private final Set<String> fileNames;
    private final Set<String> methodNames;
    private final Set<String> classNames;

    public SearchCriteria() {
        languages = new HashSet<>();
        this.tags = new HashSet<>();
        this.fileNames = new HashSet<>();
        this.methodNames = new HashSet<>();
        this.classNames = new HashSet<>();
    }

    //Adds to all sets except languages
    public void addAll(List<String> s) {
        tags.addAll(s);
        fileNames.addAll(s);
        methodNames.addAll(s);
        classNames.addAll(s);
    }
}


