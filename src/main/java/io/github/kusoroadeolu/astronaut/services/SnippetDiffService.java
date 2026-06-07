package io.github.kusoroadeolu.astronaut.services;

import com.github.difflib.UnifiedDiffUtils;
import io.github.kusoroadeolu.astronaut.dtos.SnippetContentResponse;
import io.github.kusoroadeolu.astronaut.dtos.diffs.ChangeType;
import io.github.kusoroadeolu.astronaut.dtos.diffs.DiffLine;
import io.github.kusoroadeolu.astronaut.dtos.diffs.SnippetDiff;
import io.github.kusoroadeolu.astronaut.dtos.diffs.SnippetDiffPair;
import io.github.kusoroadeolu.astronaut.exceptions.SnippetComparisonException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Subtask;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class SnippetDiffService {

    private final SnippetCrudService snippetCrudService;
    private final static String REVISED = "+++";
    private final static String ORIGINAL = "---";

    /**
     * Compares two snippets together, generates their diffs and returns a detailed dto {@link SnippetDiffPair} containing the changes between both snippets
     * @param comparingId The ID of the snippet we're comparing
     * @param comparingToId The ID of the snippet we're comparing against
     * @return a detailed dto {@link SnippetDiffPair} containing the changes between both snippets
     */
    public SnippetDiffPair generateSnippetDiff(String comparingId, String comparingToId){
        log.info("Comparing two snippets with ID: {} and ID: {}", comparingId, comparingId);
        SnippetContentResponse comparing;
        SnippetContentResponse comparingTo;

        try (var scope = StructuredTaskScope.open()) {
            Subtask<SnippetContentResponse> var1 = scope.fork(() -> snippetCrudService.findById(comparingId));
            Subtask<SnippetContentResponse> var2 = scope.fork(() -> snippetCrudService.findById(comparingToId));
            scope.join();

            comparing = var1.get();
            comparingTo = var2.get();
        } catch (InterruptedException e) {
            throw new SnippetComparisonException("Failed to compare snippets with ID: %s and %s".formatted(comparingId, comparingToId), e);
        }

        final List<String> comparingContentAsList = List.of(comparing.content().split("\n"));
        final List<String> comparingToContentAsList = List.of(comparingTo.content().split("\n"));

        final List<String> unifiedDiffs = UnifiedDiffUtils.generateOriginalAndDiff(comparingContentAsList, comparingToContentAsList);
        log.info("Successfully compared two snippets with ID: {} and ID: {}", comparingId, comparingToId);
        return parseSnippetDiffs(comparing.response().name(), comparingTo.response().name(), unifiedDiffs);
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