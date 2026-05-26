package io.github.kusoroadeolu.astronaut.dtos.diffs;

public record SnippetDiffPair (
        SnippetDiff comparing,
        SnippetDiff comparingTo
){
}
