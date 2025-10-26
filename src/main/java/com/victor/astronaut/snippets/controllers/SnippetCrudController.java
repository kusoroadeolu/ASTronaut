package com.victor.astronaut.snippets.controllers;

import com.victor.astronaut.appuser.entites.AppUserPrincipal;
import com.victor.astronaut.exceptions.ApiError;
import com.victor.astronaut.snippets.dtos.SnippetCreationRequest;
import com.victor.astronaut.snippets.dtos.SnippetResponse;
import com.victor.astronaut.snippets.dtos.SnippetUpdateRequest;
import com.victor.astronaut.snippets.projections.SnippetPreview;
import com.victor.astronaut.snippets.services.SnippetCrudService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/snippets")
@Tag(name = "Snippet Management", description = "CRUD operations for code snippets")
@PreAuthorize("hasAnyRole('APP_USER', 'APP_ADMIN')")
@ApiResponses(value = {
        @ApiResponse(responseCode = "401", description = "User is not authenticated", content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "500", description = "An unexpected error occurred", content = @Content(schema = @Schema(implementation = ApiError.class)))
})
public class SnippetCrudController {

    private final SnippetCrudService snippetCrudService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a snippet", description = "Creates a new code snippet for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Snippet created successfully", content = @Content(schema = @Schema(implementation = SnippetResponse.class)))
    })
    public ResponseEntity<SnippetResponse> createSnippet(
            @RequestBody @Valid SnippetCreationRequest request,
            @AuthenticationPrincipal AppUserPrincipal principal
    ){
        SnippetResponse response = snippetCrudService.createSnippet(principal.getId(), request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update a snippet", description = "Updates an existing snippet's metadata")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Snippet updated successfully", content = @Content(schema = @Schema(implementation = SnippetResponse.class))),
            @ApiResponse(responseCode = "404", description = "Snippet not found", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<SnippetResponse> updateSnippet(
            @Parameter(description = "Snippet ID", required = true) @PathVariable("id") long id,
            @RequestBody @Valid SnippetUpdateRequest request,
            @AuthenticationPrincipal AppUserPrincipal principal
    ){
        SnippetResponse response = snippetCrudService.updateSnippet(id, principal.getId(), request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a snippet", description = "Deletes a snippet by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Snippet deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Snippet not found", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<Void> deleteSnippet(
            @Parameter(description = "Snippet ID", required = true) @PathVariable("id") long id,
            @AuthenticationPrincipal AppUserPrincipal principal
    ){
        snippetCrudService.deleteSnippet(principal.getId(), id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get a snippet by ID", description = "Retrieves a specific snippet by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Snippet found", content = @Content(schema = @Schema(implementation = SnippetResponse.class))),
            @ApiResponse(responseCode = "404", description = "Snippet not found", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<SnippetResponse> findSnippetById(
            @Parameter(description = "Snippet ID", required = true) @PathVariable("id") long id,
            @AuthenticationPrincipal AppUserPrincipal principal
    ){
        SnippetResponse response = snippetCrudService.findById(principal.getId(), id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all snippets", description = "Retrieves paginated list of all snippets for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Snippets retrieved successfully")
    })
    public ResponseEntity<Page<SnippetPreview>> findSnippetsByUser(
            @AuthenticationPrincipal AppUserPrincipal principal,
            @ParameterObject
            @PageableDefault(size = 20, sort = "updatedAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ){
        Page<SnippetPreview> responses = this.snippetCrudService.findSnippetsByUser(principal.getId(), pageable);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }
}