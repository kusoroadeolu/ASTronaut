package com.victor.astronaut.snippets.impl;

import com.victor.astronaut.snippets.Snippet;
import com.victor.astronaut.snippets.dto.SnippetResponse;
import com.victor.astronaut.snippets.enums.SnippetLanguage;
import com.victor.astronaut.snippets.projections.SnippetPreview;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

@Service
public class SnippetMapper {
    public SnippetResponse toResponse(Snippet snippet){
        return new SnippetResponse(snippet.getId(), snippet.getTags() ,snippet.getName(), snippet.getContent(), snippet.getExtraNotes(), snippet.isDraft(), snippet.getLanguage().getLanguage() ,snippet.getCreatedAt(), snippet.getUpdatedAt());
    }

    public SnippetPreview toPreview(Snippet snippet){
        return new SnippetPreview() {
            @Override
            public Long getId() {
              return snippet.getId();
            }

            @Override
            public String getName() {
                return snippet.getName();
            }

            @Override
            public Set<String> getTags() {
                return snippet.getTags();
            }

            @Override
            public SnippetLanguage getLanguage() {
                return snippet.getLanguage();
            }

            @Override
            public LocalDateTime getCreatedAt() {
                return snippet.getCreatedAt();
            }

            @Override
            public LocalDateTime getUpdatedAt(){
                return snippet.getUpdatedAt();
            }
        };
    }
}
