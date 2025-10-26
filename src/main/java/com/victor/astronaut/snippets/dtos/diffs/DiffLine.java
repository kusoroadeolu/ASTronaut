package com.victor.astronaut.snippets.dtos.diffs;

public record DiffLine(
        int lineNum,
        String lineContent,
        ChangeType lineType
){

}

