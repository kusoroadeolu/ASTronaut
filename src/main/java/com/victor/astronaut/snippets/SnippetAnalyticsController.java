package com.victor.astronaut.snippets;

import com.victor.astronaut.appuser.AppUserPrincipal;
import com.victor.astronaut.exceptions.ApiError;
import com.victor.astronaut.snippets.dto.SearchCriteria;
import com.victor.astronaut.snippets.dto.diffs.SnippetDiffPair;
import com.victor.astronaut.snippets.projections.SnippetPreview;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/snippets")
@Tag(name = "Snippet Analytics", description = "Search, filter, and compare snippets")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('APP_USER', 'APP_ADMIN')")
@ApiResponses(value = {
        @ApiResponse(responseCode = "401", description = "User is not authenticated", content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "500", description = "An unexpected error occurred", content = @Content(schema = @Schema(implementation = ApiError.class)))
})
public class SnippetAnalyticsController {

    private final SnippetQueryService queryService;
    private final SnippetDiffService diffService;

    @PostMapping("/filter")
    @Operation(summary = "Filter snippets", description = "Searches and filters snippets based on provided criteria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Snippets filtered successfully")
    })
    public ResponseEntity<Page<SnippetPreview>> filterSnippets(
            @RequestBody SearchCriteria searchCriteria,
            @ParameterObject
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable,
            @AuthenticationPrincipal AppUserPrincipal principal
    ){
        return ResponseEntity.ok(this.queryService.searchBasedOnCriteria(principal.getId(), searchCriteria, pageable));
    }

    @GetMapping("/{id}/compare/{comparingToId}")
    @Operation(summary = "Compare two snippets", description = "Generates a diff comparison between two snippets")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Snippets compared successfully", content = @Content(schema = @Schema(implementation = SnippetDiffPair.class))),
            @ApiResponse(responseCode = "404", description = "One or both snippets not found", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<SnippetDiffPair> compareSnippets(
            @Parameter(description = "First snippet ID", required = true) @PathVariable("id") long comparingId,
            @Parameter(description = "Second snippet ID to compare against", required = true) @PathVariable("comparingToId") long comparingToId,
            @AuthenticationPrincipal AppUserPrincipal userPrincipal
    ) {
        return ResponseEntity.ok(this.diffService.generateSnippetDiff(userPrincipal.getId(), comparingId, comparingToId));
    }
}