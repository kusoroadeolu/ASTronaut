package com.victor.astronaut.snippets.dtos.diffs;

import java.util.List;

public record SnippetDiff(
        String snippetName,
        List<DiffLine> lines
) {

}
