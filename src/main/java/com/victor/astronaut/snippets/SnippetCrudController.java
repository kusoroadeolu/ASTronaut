package com.victor.astronaut.snippets;

import com.victor.astronaut.appuser.AppUserPrincipal;
import com.victor.astronaut.snippets.dto.SnippetCreationRequest;
import com.victor.astronaut.snippets.dto.SnippetResponse;
import com.victor.astronaut.snippets.dto.SnippetUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@PreAuthorize("hasAnyRole('APP_USER', 'APP_ADMIN')")
public class SnippetCrudController {

    private final SnippetCrudService snippetCrudService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<SnippetResponse> createSnippet(@RequestBody @Valid SnippetCreationRequest request, @AuthenticationPrincipal AppUserPrincipal principal){
        SnippetResponse response = snippetCrudService.createSnippet(principal.getId(), request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<SnippetResponse> updateSnippet(@PathVariable("id") long id, @RequestBody @Valid SnippetUpdateRequest request, @AuthenticationPrincipal AppUserPrincipal principal){
        SnippetResponse response = snippetCrudService.updateSnippet(id, principal.getId(), request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.GONE)
    public ResponseEntity<Void> deleteSnippet(@PathVariable("id") long id, @AuthenticationPrincipal AppUserPrincipal principal){
        snippetCrudService.deleteSnippet(principal.getId(), id);
        return new ResponseEntity<>(HttpStatus.GONE);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<SnippetResponse> findSnippetById(@PathVariable("id") long id, @AuthenticationPrincipal AppUserPrincipal principal){
        SnippetResponse response = snippetCrudService.findById(principal.getId(), id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Page<SnippetResponse>> findSnippetsByUser(
            @AuthenticationPrincipal AppUserPrincipal principal,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ){
        Page<SnippetResponse> responses = this.snippetCrudService.findSnippetsByUser(principal.getId(), pageable);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }



}
