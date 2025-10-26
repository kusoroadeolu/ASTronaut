package com.victor.astronaut.snippets.services.impl;

import com.github.difflib.UnifiedDiffUtils;
import com.victor.astronaut.snippets.entities.Snippet;
import com.victor.astronaut.snippets.services.SnippetCrudService;
import com.victor.astronaut.snippets.services.SnippetDiffService;
import com.victor.astronaut.snippets.dtos.diffs.ChangeType;
import com.victor.astronaut.snippets.dtos.diffs.DiffLine;
import com.victor.astronaut.snippets.dtos.diffs.SnippetDiff;
import com.victor.astronaut.snippets.dtos.diffs.SnippetDiffPair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class SnippetDiffServiceImpl implements SnippetDiffService {

    private final SnippetCrudService snippetCrudService;
    private final static String REVISED = "+++";
    private final static String ORIGINAL = "---";

    /**
     * Compares two snippets together, generates their diffs and returns a detailed dto {@link SnippetDiffPair} containing the changes between both snippets
     * @param appUserId The ID of the user
     * @param comparingId The ID of the snippet we're comparing
     * @param comparingToId The ID of the snippet we're comparing against
     * @return a detailed dto {@link SnippetDiffPair} containing the changes between both snippets
     */
    @Override
    public SnippetDiffPair generateSnippetDiff(long appUserId, long comparingId, long comparingToId){
        log.info("Comparing two snippets with ID: {} and ID: {}", comparingId, comparingId);
        final Snippet comparing = this.snippetCrudService.findByAppUserIdAndId(appUserId, comparingId);
        final Snippet comparingTo = this.snippetCrudService.findByAppUserIdAndId(appUserId, comparingToId);

        final List<String> comparingContentAsList = Arrays.asList(comparing.getContent().split("\n"));
        final List<String> comparingToContentAsList = Arrays.asList(comparingTo.getContent().split("\n"));

        final List<String> unifiedDiffs = UnifiedDiffUtils.generateOriginalAndDiff(comparingContentAsList, comparingToContentAsList);
        log.info("Successfully compared two snippets with ID: {} and ID: {}", comparingId, comparingToId);
        return this.parseSnippetDiffs(comparing.getName(), comparingTo.getName(), unifiedDiffs);
    }


    //Parses the unified diff to a SnippetDiffPair object
    private SnippetDiffPair parseSnippetDiffs(String comparingName, String comparingToName, List<String> unifiedDiffs) {

        DiffLine cDiffLine = null;
        DiffLine ctDiffLine = null;

        SnippetDiff comparingDiff = null;
        SnippetDiff comparingToDiff = null;

        int cCount = 0;
        int ctCount = 0;
        final String patchPattern = "^@@\\s+-\\d+(?:,\\d+)?\\s+\\+\\d+(?:,\\d+)?\\s+@@";

        final List<DiffLine> cDiffLines = new ArrayList<>();
        final List<DiffLine> ctDiffLines = new ArrayList<>();


        for (String line : unifiedDiffs){
            if (Pattern.matches(patchPattern, line) || line.startsWith(ORIGINAL) || line.startsWith(REVISED)){
                continue;
            }

            //Checks if a line was unchanged
            if(line.charAt(0) == ChangeType.UNCHANGED.getChangeSymbol()){
                line = line.substring(1);
                cDiffLine = new DiffLine(cCount, line, ChangeType.UNCHANGED);
                ctDiffLine = new DiffLine(ctCount, line, ChangeType.UNCHANGED);

                cDiffLines.add(cDiffLine);
                ctDiffLines.add(ctDiffLine);

                cCount++;
                ctCount++;
                //Checks if we come across a line in the ct snippet that was added
            } else if (line.charAt(0) == ChangeType.ADDED.getChangeSymbol()) {
                line = line.substring(1);
                ctDiffLine = new DiffLine(ctCount, line, ChangeType.ADDED);
                ctDiffLines.add(ctDiffLine);
                ctCount++;
                //Checks if we come across a line in the c snippet that was removed
            }else if (line.charAt(0) == ChangeType.REMOVED.getChangeSymbol()){
                line = line.substring(1);
                cDiffLine = new DiffLine(cCount, line, ChangeType.REMOVED);
                cDiffLines.add(cDiffLine);
                cCount++;
            }

        }

        comparingDiff = new SnippetDiff(comparingName, cDiffLines);
        comparingToDiff = new SnippetDiff(comparingToName, ctDiffLines);

        return new SnippetDiffPair(comparingDiff, comparingToDiff);
    }


}
