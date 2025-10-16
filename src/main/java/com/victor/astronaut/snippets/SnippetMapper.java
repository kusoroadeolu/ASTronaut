package com.victor.astronaut.snippets;

import com.victor.astronaut.snippets.dto.SnippetResponse;
import org.springframework.stereotype.Service;

@Service
public class SnippetMapper {
    public SnippetResponse toResponse(Snippet snippet){
        return new SnippetResponse(snippet.getName(), snippet.getContent(), snippet.getExtraNotes(), snippet.isDraft() ,snippet.getCreatedAt(), snippet.getUpdatedAt());
    }
}
