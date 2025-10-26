package com.victor.astronaut.snippets.specifications;

import com.victor.astronaut.appuser.entites.AppUser;
import com.victor.astronaut.snippets.entities.Snippet;
import com.victor.astronaut.snippets.enums.SnippetLanguage;
import org.springframework.data.jpa.domain.Specification;

import java.util.Set;

public interface SnippetSpecBuilder {
    SnippetSpecBuilder hasUser(AppUser a);

    SnippetSpecBuilder hasAnyLanguage(Set<SnippetLanguage> expectedLangs);

    SnippetSpecBuilder hasValFromElementCollection(String fieldName, Set<String> expectedVals);

    SnippetSpecBuilder hasTagOrName(Set<String> expectedTagsOrNames);

    Specification<Snippet> build();

    //Simple factory method
    static SnippetSpecBuilder typeOf(SpecType type){
        switch (type) {
            case FUZZY -> {
                return FuzzySnippetSpecBuilder.builder();
            }
            case DIRECT -> {
                return DirectSnippetSpecBuilder.builder();
            }
            default -> throw new IllegalArgumentException("Illegal enum type");
        }
    }


    enum SpecType{
        FUZZY,
        DIRECT;
    }
}
