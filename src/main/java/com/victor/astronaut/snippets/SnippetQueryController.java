package com.victor.astronaut.snippets;

import com.victor.astronaut.appuser.AppUserPrincipal;
import com.victor.astronaut.snippets.dto.SearchFilterDto;
import com.victor.astronaut.snippets.dto.SnippetResponse;
import com.victor.astronaut.snippets.impl.SnippetQueryServiceImpl;
import com.victor.astronaut.snippets.projections.SnippetPreview;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/snippets")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('APP_USER', 'APP_ADMIN')")
public class SnippetQueryController {

    private final SnippetQueryServiceImpl queryService;

    @GetMapping("/search")
    public ResponseEntity<Page<SnippetPreview>> findSnips(@RequestBody SearchFilterDto filterDto, @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable, @AuthenticationPrincipal AppUserPrincipal principal, Sort sort){
        return ResponseEntity.ok(queryService.searchBasedOnCriteria(principal.getId(), filterDto, pageable));
    }

}
