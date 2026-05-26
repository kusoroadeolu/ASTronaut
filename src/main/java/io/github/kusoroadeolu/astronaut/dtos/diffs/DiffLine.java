package io.github.kusoroadeolu.astronaut.dtos.diffs;

public record DiffLine(
        int lineNum,
        String lineContent,
        ChangeType lineType
){

}

