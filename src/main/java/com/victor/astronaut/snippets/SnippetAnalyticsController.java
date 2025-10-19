package com.victor.astronaut.snippets;

import com.victor.astronaut.appuser.AppUserPrincipal;
import com.victor.astronaut.snippets.dto.SearchCriteria;
import com.victor.astronaut.snippets.dto.diffs.SnippetDiffPair;
import com.victor.astronaut.snippets.projections.SnippetPreview;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('APP_USER', 'APP_ADMIN')")
public class SnippetAnalyticsController {

    private final SnippetQueryService queryService;
    private final SnippetDiffService diffService;

    @GetMapping("/filter")
    public ResponseEntity<Page<SnippetPreview>> filterSnippets(@RequestBody SearchCriteria filterDto, @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable, @AuthenticationPrincipal AppUserPrincipal principal, Sort sort){
        return ResponseEntity.ok(this.queryService.searchBasedOnCriteria(principal.getId(), filterDto, pageable));
    }

    @GetMapping("/{id}/compare/{comparingToId}")
    public ResponseEntity<SnippetDiffPair> compareSnippets(@PathVariable("id") long comparingId, @PathVariable("comparingToId") long comparingToId, @AuthenticationPrincipal AppUserPrincipal userPrincipal) {
        return ResponseEntity.ok(this.diffService.generateSnippetDiff(userPrincipal.getId(), comparingId, comparingToId));
    }

}
