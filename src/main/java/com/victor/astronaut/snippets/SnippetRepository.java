package com.victor.astronaut.snippets;

import com.victor.astronaut.appuser.AppUser;
import com.victor.astronaut.snippets.dto.SnippetResponse;
import com.victor.astronaut.snippets.projections.SnippetPreview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryRewriter;

import java.util.Optional;

public interface SnippetRepository extends JpaRepository<Snippet, Long> {
    @Modifying
    int deleteSnippetByAppUserAndId(AppUser user, long snippetId);

    Optional<Snippet> findSnippetByAppUserAndId(AppUser appUser, Long id);

    @Query("SELECT s FROM Snippet s WHERE s.appUser = :appUser")
    Page<SnippetPreview> findAllByAppUser(AppUser appUser, Pageable pageable);
}
