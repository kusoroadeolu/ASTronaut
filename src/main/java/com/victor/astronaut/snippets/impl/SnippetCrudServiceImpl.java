package com.victor.astronaut.snippets.impl;

import com.victor.astronaut.appuser.AppUser;
import com.victor.astronaut.appuser.AppUserQueryService;
import com.victor.astronaut.exceptions.NoSuchSnippetException;
import com.victor.astronaut.exceptions.SnippetPersistenceException;
import com.victor.astronaut.snippets.Snippet;
import com.victor.astronaut.snippets.SnippetCrudService;
import com.victor.astronaut.snippets.SnippetMapper;
import com.victor.astronaut.snippets.SnippetRepository;
import com.victor.astronaut.snippets.dto.SnippetCreationRequest;
import com.victor.astronaut.snippets.dto.SnippetResponse;
import com.victor.astronaut.snippets.dto.SnippetUpdateRequest;
import com.victor.astronaut.snippets.snippetparser.JavaSnippetParser;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.lang.String.format;

/**
 * Service implementation for managing code snippet CRUD operations.
 * Handles creating, reading, updating, and deleting snippets for users.
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class SnippetCrudServiceImpl implements SnippetCrudService {

    private final AppUserQueryService appUserQueryService;
    private final SnippetRepository snippetRepository;
    private final SnippetMapper snippetMapper;
    private final JavaSnippetParser javaSnippetParser;

    /**
     * Creates a new snippet for the specified user.
     *
     * @param appUserId the ID of the user creating the snippet
     * @param creationRequest the snippet creation details
     * @return the created snippet as a response DTO
     * @throws SnippetPersistenceException if creation fails due to data integrity or unexpected errors
     */
    @Transactional
    @Override
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

    /**
     * Deletes a snippet belonging to the specified user.
     *
     * @param appUserId the ID of the user who owns the snippet
     * @param snippetId the ID of the snippet to delete
     * @throws SnippetPersistenceException if deletion fails or snippet doesn't exist/belong to user
     */
    @Transactional
    @Override
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

    /**
     * Updates an existing snippet with new content and metadata.
     *
     * @param snippetId the ID of the snippet to update
     * @param appUserId the ID of the user who owns the snippet
     * @param updateRequest the updated snippet details
     * @return the updated snippet as a response DTO
     * @throws NoSuchSnippetException if snippet doesn't exist or doesn't belong to user
     * @throws SnippetPersistenceException if update fails due to data integrity or unexpected errors
     */
    @Transactional
    @Override
    public SnippetResponse updateSnippet(long snippetId, long appUserId, @NonNull SnippetUpdateRequest updateRequest){
        try{
            log.info("Attempting to update snippet: {} for user with ID: {}", updateRequest.snippetName() ,appUserId);
            final AppUser user = this.appUserQueryService.findById(appUserId);
            Snippet found = snippetRepository.findSnippetByAppUserAndId(user, snippetId)
                    .orElseThrow(() -> new NoSuchSnippetException(String.format("Failed to find snippet with ID: %s belonging to user with ID: %s", snippetId, appUserId)));
            this.modifySnippet(found ,updateRequest);
            final Snippet saved = this.snippetRepository.save(found);
            //Extract the metadata from the snippet
            this.javaSnippetParser.parseSnippetContent(saved);
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

    /**
     * Finds a snippet by its ID for the specified user.
     *
     * @param appUserId the ID of the user who owns the snippet
     * @param snippetId the ID of the snippet to find
     * @return the found snippet as a response DTO
     * @throws NoSuchSnippetException if snippet doesn't exist or doesn't belong to user
     */
    @Transactional(readOnly = true)
    @Override
    public SnippetResponse findById(long appUserId, long snippetId){
        log.info("Attempting to find snippet with ID: {} for user with ID: {}", snippetId ,appUserId);
        final AppUser user = this.appUserQueryService.findById(appUserId);
        final Snippet found = snippetRepository.findSnippetByAppUserAndId(user, snippetId)
                .orElseThrow(() -> new NoSuchSnippetException(String.format("Failed to find snippet with ID: %s belonging to user with ID: %s", snippetId, appUserId)));
        log.info("Successfully found snippet: {} for user with ID: {}", found.getName() ,appUserId);
        return this.snippetMapper.toResponse(found);
    }

    /**
     * Retrieves all snippets belonging to a user with pagination.
     *
     * @param appUserId the ID of the user whose snippets to retrieve
     * @param pageable pagination information
     * @return a page of snippet responses
     */
    @Transactional(readOnly = true)
    @Override
    public Page<SnippetResponse> findSnippetsByUser(long appUserId,
                                                    Pageable pageable){
        final AppUser user = this.appUserQueryService.findById(appUserId);
        return this.snippetRepository.findAllByAppUser(user, pageable);
    }

    /**
     * Builds a new Snippet entity from creation request data.
     *
     * @param appUser the user creating the snippet
     * @param creationRequest the snippet creation details
     * @return a new Snippet entity
     */
    public Snippet buildSnippet(AppUser appUser, SnippetCreationRequest creationRequest){
        return Snippet
                .builder()
                .name(creationRequest.snippetName())
                .tags(creationRequest.tags())
                .appUser(appUser)
                .build();
    }

    /**
     * Modifies an existing snippet with updated values.
     * Sets the snippet as draft if content is blank.
     *
     * @param snippet the snippet to modify
     * @param updateRequest the updated values
     */
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