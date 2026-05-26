package io.github.kusoroadeolu.astronaut.controllers;

import io.github.kusoroadeolu.astronaut.dtos.SnippetResponse;
import io.github.kusoroadeolu.astronaut.dtos.diffs.SnippetDiffPair;
import io.github.kusoroadeolu.astronaut.exceptions.ApiError;
import io.github.kusoroadeolu.astronaut.services.SnippetDiffService;
import io.github.kusoroadeolu.astronaut.services.SnippetQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/snippets")
@Tag(name = "SnippetIndex Analytics", description = "Search, filter, and compare snippets")
@RequiredArgsConstructor
@ApiResponses(value = {
        @ApiResponse(responseCode = "401", description = "User is not authenticated", content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "500", description = "An unexpected error occurred", content = @Content(schema = @Schema(implementation = ApiError.class)))
})
public class SnippetQueryController {

    private final SnippetQueryService queryService;
    private final SnippetDiffService diffService;

    @GetMapping("/search")
    @Operation(summary = "Search snippets", description = "Searches snippets based on provided criteria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Snippets filtered successfully")
    })
    public ResponseEntity<List<SnippetResponse>> filterSnippets(@RequestParam("query") String query){
        return ResponseEntity.ok(queryService.searchBasedOnCriteria(query));
    }

    @GetMapping("/{id}/compare/{comparingToId}")
    @Operation(summary = "Compare two snippets", description = "Generates a diff comparison between two snippets")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Snippets compared successfully", content = @Content(schema = @Schema(implementation = SnippetDiffPair.class))),
            @ApiResponse(responseCode = "404", description = "One or both snippets not found", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<SnippetDiffPair> compareSnippets(
            @Parameter(description = "First snippet ID", required = true) @PathVariable("id") String comparingId,
            @Parameter(description = "Second snippet ID to compare against", required = true) @PathVariable("comparingToId") String comparingToId
    ) {
        return ResponseEntity.ok(diffService.generateSnippetDiff(comparingId, comparingToId));
    }
}