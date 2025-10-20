package com.victor.astronaut.snippets.dto.diffs;

public record SnippetDiffPair (
        SnippetDiff comparing,
        SnippetDiff comparingTo
){
}
