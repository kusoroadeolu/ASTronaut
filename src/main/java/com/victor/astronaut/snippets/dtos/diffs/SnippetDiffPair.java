package com.victor.astronaut.snippets.dtos.diffs;

public record SnippetDiffPair (
        SnippetDiff comparing,
        SnippetDiff comparingTo
){
}
