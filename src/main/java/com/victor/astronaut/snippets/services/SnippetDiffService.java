package com.victor.astronaut.snippets.services;

import com.victor.astronaut.snippets.dtos.diffs.SnippetDiffPair;

public interface SnippetDiffService {
    SnippetDiffPair generateSnippetDiff(long appUserId, long comparingId, long comparingToId);
}
