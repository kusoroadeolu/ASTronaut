package com.victor.astronaut.snippets.dto.diffs;

public record DiffLine(
        int lineNum,
        String lineContent,
        ChangeType lineType
){

}

