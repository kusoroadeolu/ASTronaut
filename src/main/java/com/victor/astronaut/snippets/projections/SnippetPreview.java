package com.victor.astronaut.snippets.projections;

import com.victor.astronaut.snippets.SnippetLanguage;

import java.time.LocalDateTime;
import java.util.Set;

public interface SnippetPreview {
    Integer getId();
    String getName();
    Set<String> getTags();
    SnippetLanguage getLanguage();
    LocalDateTime getCreatedAt();

}
