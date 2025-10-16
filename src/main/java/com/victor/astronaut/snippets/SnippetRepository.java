package com.victor.astronaut.snippets;

import com.victor.astronaut.appuser.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

public interface SnippetRepository extends JpaRepository<Snippet, Long> {
    @Modifying
    int deleteSnippetByAppUserAndId(AppUser user, long snippetId);

    Optional<Snippet> findSnippetByAppUserAndId(AppUser appUser, Long id);
}
