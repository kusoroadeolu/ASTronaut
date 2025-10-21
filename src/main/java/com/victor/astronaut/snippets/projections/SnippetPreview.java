package com.victor.astronaut.snippets.projections;

import com.victor.astronaut.snippets.enums.SnippetLanguage;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.Set;

public interface SnippetPreview {
    Long getId();
    String getName();
    Set<String> getTags();
    SnippetLanguage getLanguage();
    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();

}
