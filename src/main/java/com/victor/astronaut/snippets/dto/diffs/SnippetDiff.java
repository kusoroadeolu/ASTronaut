package com.victor.astronaut.snippets.dto.diffs;

import java.util.List;

public record SnippetDiff(
        String snippetName,
        List<DiffLine> lines
) {

}
