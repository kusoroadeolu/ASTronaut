package io.github.kusoroadeolu.astronaut.dtos.diffs;

import java.util.List;

public record SnippetDiff(
        String snippetName,
        List<DiffLine> lines
) {

}
