package com.victor.astronaut.snippets.repos;

import com.victor.astronaut.appuser.entites.AppUser;
import com.victor.astronaut.snippets.entities.Snippet;
import com.victor.astronaut.snippets.projections.SnippetPreview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;

import java.util.Optional;

public interface SnippetRepository extends JpaRepository<Snippet, Long>, JpaSpecificationExecutor<Snippet> {
    @Modifying
    Integer deleteSnippetByAppUserAndId(AppUser user, long snippetId);

    Optional<Snippet> findSnippetByAppUserAndId(AppUser appUser, Long id);

    @Query("SELECT s FROM Snippet s WHERE s.appUser = :appUser")
    Page<SnippetPreview> findAllByAppUser(AppUser appUser, Pageable pageable);

}
