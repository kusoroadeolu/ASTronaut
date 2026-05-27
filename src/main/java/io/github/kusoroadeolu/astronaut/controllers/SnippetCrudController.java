package io.github.kusoroadeolu.astronaut.controllers;

import io.github.kusoroadeolu.astronaut.dtos.SnippetContentResponse;
import io.github.kusoroadeolu.astronaut.dtos.SnippetCreationRequest;
import io.github.kusoroadeolu.astronaut.dtos.SnippetResponse;
import io.github.kusoroadeolu.astronaut.dtos.SnippetUpdateRequest;
import io.github.kusoroadeolu.astronaut.exceptions.ApiError;
import io.github.kusoroadeolu.astronaut.services.SnippetCrudService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/snippets")
@Tag(name = "Snippet Management", description = "CRUD operations for snippets")
@ApiResponses(value = {
        @ApiResponse(responseCode = "500", description = "An unexpected error occurred", content = @Content(schema = @Schema(implementation = ApiError.class)))
})
public class SnippetCrudController {

    private final SnippetCrudService snippetCrudService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a snippet", description = "Creates a new code snippet for the user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Snippet created successfully", content = @Content(schema = @Schema(implementation = SnippetResponse.class))),
            @ApiResponse(responseCode = "403", description = "Invalid github auth Token", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<SnippetResponse> createSnippet(
            @RequestBody @Valid SnippetCreationRequest request
    ){
        SnippetResponse fileResponse = snippetCrudService.createSnippet(request);
        return new ResponseEntity<>(fileResponse, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update a snippet", description = "Updates an existing snippet's metadata")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Snippet updated successfully", content = @Content(schema = @Schema(implementation = SnippetResponse.class))),
            @ApiResponse(responseCode = "404", description = "Snippet not found", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "403", description = "Failed to authorize github user", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<SnippetResponse> updateSnippet(
            @Parameter(description = "Snippet Id", required = true) @PathVariable("id") String id,
            @RequestBody @Valid SnippetUpdateRequest request
    ){
        SnippetResponse fileResponse = snippetCrudService.updateSnippet(id, request);
        return new ResponseEntity<>(fileResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a snippet", description = "Deletes a snippet by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Snippet deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Snippet not found", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "403", description = "Failed to authorize github user", content = @Content(schema = @Schema(implementation = ApiError.class)))

    })
    public ResponseEntity<Void> deleteSnippet(
            @Parameter(description = "SnippetIndex Id", required = true) @PathVariable("id") String id){
        snippetCrudService.deleteSnippet(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get a snippet by ID", description = "Retrieves a specific snippet by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SnippetIndex found", content = @Content(schema = @Schema(implementation = SnippetContentResponse.class))),
            @ApiResponse(responseCode = "404", description = "Snippet not found", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "403", description = "Failed to authorize github user", content = @Content(schema = @Schema(implementation = ApiError.class)))

    })
    public ResponseEntity<SnippetContentResponse> findSnippetById(
            @Parameter(description = "Snippet Id", required = true) @PathVariable("id") String id
    ){
        SnippetContentResponse fileResponse = snippetCrudService.findById(id);
        return new ResponseEntity<>(fileResponse, HttpStatus.OK);
    }

    @GetMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Refreshes all snippets from the user's gist page", description = "Retrieves a list of all snippets")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Snippets retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Failed to authorize github user", content = @Content(schema = @Schema(implementation = ApiError.class)))

    })
    public ResponseEntity<List<SnippetResponse>> refreshGists(){
        List<SnippetResponse> responses = this.snippetCrudService.refreshGists();
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all snippets", description = "Retrieves a list of all snippets")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Snippets retrieved successfully")
    })
    public ResponseEntity<List<SnippetResponse>> getAllSnippets(@RequestParam(value = "order_by", defaultValue = "updated_at") String order){
        List<SnippetResponse> responses = this.snippetCrudService.getSnippets(order);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }


}