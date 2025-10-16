package com.victor.astronaut.snippets.impl;

import com.victor.astronaut.appuser.AppUser;
import com.victor.astronaut.appuser.AppUserQueryService;
import com.victor.astronaut.exceptions.NoSuchSnippetException;
import com.victor.astronaut.exceptions.SnippetPersistenceException;
import com.victor.astronaut.snippets.Snippet;
import com.victor.astronaut.snippets.SnippetMapper;
import com.victor.astronaut.snippets.SnippetRepository;
import com.victor.astronaut.snippets.dto.SnippetCreationRequest;
import com.victor.astronaut.snippets.dto.SnippetResponse;
import com.victor.astronaut.snippets.dto.SnippetUpdateRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.lang.String.format;

@RequiredArgsConstructor
@Service
@Slf4j
public class SnippetCrudServiceImpl {

    private final AppUserQueryService appUserQueryService;
    private final SnippetRepository snippetRepository;
    private final SnippetMapper snippetMapper;

    @Transactional
    public SnippetResponse createSnippet(long appUserId, @NonNull SnippetCreationRequest creationRequest){
        try{
            log.info("Attempting to create snippet: {} for user with ID: {}", creationRequest.snippetName() ,appUserId);
            final AppUser user = this.appUserQueryService.findById(appUserId);
            final Snippet saved = this.snippetRepository.save(this.buildSnippet(user, creationRequest));
            log.info("Successfully created snippet: {} for user with ID: {}", creationRequest.snippetName() ,appUserId);
            return this.snippetMapper.toResponse(saved);
        }catch (DataIntegrityViolationException e){
            log.error("A data integrity error occurred while trying to create snippet: {}", creationRequest.snippetName());
            throw new SnippetPersistenceException(format("A data integrity error occurred while trying to create snippet: %s", creationRequest.snippetName()));
        }catch (Exception e){
            log.error("An unexpected error occurred while trying to create snippet: {}", creationRequest.snippetName());
            throw new SnippetPersistenceException(format("An unexpected error occurred while trying to create snippet: %s", creationRequest.snippetName()));
        }
    }

    @Transactional
    public void deleteSnippet(long appUserId, long snippetId){
        try{
            log.info("Attempting to delete snippet with ID: {} for user with ID: {}", snippetId ,appUserId);
            final AppUser user = this.appUserQueryService.findById(appUserId);
            final int count = this.snippetRepository.deleteSnippetByAppUserAndId(user, snippetId);

            switch (count){
                case 1 -> log.info("Successfully deleted snippet with ID: {} for user with ID: {}", snippetId ,appUserId);
                case 0 -> throw new SnippetPersistenceException(
                        format("Failed to delete snippet with ID: %s either because it does not exist or does not belong to user with ID: %s", snippetId, appUserId)
                );
            }

        }catch (DataIntegrityViolationException e){
            log.error("A data integrity error occurred while trying to delete snippet with ID: {}", snippetId);
            throw new SnippetPersistenceException(format("A data integrity error occurred while trying to delete snippet with ID: %s", snippetId));
        }catch (Exception e){
            log.error("An unexpected error occurred while trying to delete snippet with ID: {}", snippetId);
            throw new SnippetPersistenceException(format("An unexpected error occurred while trying to delete snippet with ID: %s", snippetId));
        }
    }


    @Transactional
    public SnippetResponse updateSnippet(long snippetId, long appUserId, @NonNull SnippetUpdateRequest updateRequest){
        try{
            log.info("Attempting to update snippet: {} for user with ID: {}", updateRequest.snippetName() ,appUserId);
            final AppUser user = this.appUserQueryService.findById(appUserId);
            Snippet found = snippetRepository.findSnippetByAppUserAndId(user, snippetId)
                    .orElseThrow(() -> new NoSuchSnippetException(String.format("Failed to find snippet with ID: %s belonging to user with ID: %s", snippetId, appUserId)));
            this.modifySnippet(found ,updateRequest);
            final Snippet saved = this.snippetRepository.save(found);
            log.info("Successfully updated snippet: {} for user with ID: {}", updateRequest.snippetName() ,appUserId);
            return this.snippetMapper.toResponse(saved);
        }catch (DataIntegrityViolationException e){
            log.error("A data integrity error occurred while trying to update snippet: {}", updateRequest.snippetName());
            throw new SnippetPersistenceException(format("A data integrity error occurred while trying to update snippet: %s", updateRequest.snippetName()));
        }catch (Exception e){
            log.error("An unexpected error occurred while trying to update snippet: {}", updateRequest.snippetName());
            throw new SnippetPersistenceException(format("An unexpected error occurred while trying to update snippet: %s", updateRequest.snippetName()));
        }
    }



    @Transactional(readOnly = true)
    public SnippetResponse findById(long appUserId, long snippetId){
        log.info("Attempting to find snippet with ID: {} for user with ID: {}", snippetId ,appUserId);
        final AppUser user = this.appUserQueryService.findById(appUserId);
        final Snippet found = snippetRepository.findSnippetByAppUserAndId(user, snippetId)
                .orElseThrow(() -> new NoSuchSnippetException(String.format("Failed to find snippet with ID: %s belonging to user with ID: %s", snippetId, appUserId)));
        log.info("Successfully found snippet: {} for user with ID: {}", found.getName() ,appUserId);
        return this.snippetMapper.toResponse(found);
    }



    public Snippet buildSnippet(AppUser appUser, SnippetCreationRequest creationRequest){
        return Snippet
                .builder()
                .name(creationRequest.snippetName())
                .tags(creationRequest.tags())
                .appUser(appUser)
                .build();
    }

    public void modifySnippet(Snippet snippet, SnippetUpdateRequest updateRequest){
        String content = updateRequest.content();
        boolean isDraft = content.isBlank();
        snippet.setContent(content);
        snippet.setName(updateRequest.snippetName());
        snippet.setTags(updateRequest.tags());
        snippet.setDraft(isDraft);
        snippet.setExtraNotes(updateRequest.extraNotes());
    }

}
