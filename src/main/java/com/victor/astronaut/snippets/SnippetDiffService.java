package com.victor.astronaut.snippets;

import com.victor.astronaut.snippets.dto.diffs.SnippetDiffPair;

public interface SnippetDiffService {
    SnippetDiffPair generateSnippetDiff(long appUserId, long comparingId, long comparingToId);
}
