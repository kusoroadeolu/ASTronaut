package com.victor.astronaut.snippets.impl;

import com.victor.astronaut.appuser.AppUser;
import com.victor.astronaut.appuser.AppUserQueryService;
import com.victor.astronaut.exceptions.NoSuchSnippetException;
import com.victor.astronaut.exceptions.SnippetPersistenceException;
import com.victor.astronaut.snippets.*;
import com.victor.astronaut.snippets.dto.SnippetCreationRequest;
import com.victor.astronaut.snippets.dto.SnippetResponse;
import com.victor.astronaut.snippets.dto.SnippetUpdateRequest;
import com.victor.astronaut.snippets.snippetparser.SnippetParser;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.victor.astronaut.snippets.utils.SnippetUtils.*;
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
    private final SnippetParser snippetParser;

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
        return executeWithException("create", creationRequest.snippetName(), () -> {
            final AppUser user = this.appUserQueryService.findById(appUserId);
            final Snippet saved = this.snippetRepository.save(buildSnippet(user, creationRequest));
            return this.snippetMapper.toResponse(saved);
        });
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
        executeWithException("delete", snippetId, () -> {
            final AppUser user = this.appUserQueryService.findById(appUserId);
            final int count = this.snippetRepository.deleteSnippetByAppUserAndId(user, snippetId);

            switch (count){
                case 1 -> log.info("Successfully deleted snippet with ID: {} for user with ID: {}", snippetId ,appUserId);
                case 0 -> throw new SnippetPersistenceException(
                        format("Failed to delete snippet with ID: %s either because it does not exist or does not belong to user with ID: %s", snippetId, appUserId)
                );
            }
            return count;
        });
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
        return executeWithException(
                "update", snippetId, () -> {
                    final AppUser user = this.appUserQueryService.findById(appUserId);
                    Snippet found = snippetRepository.findSnippetByAppUserAndId(user, snippetId)
                            .orElseThrow(() -> new NoSuchSnippetException(String.format("Failed to find snippet with ID: %s belonging to user with ID: %s", snippetId, appUserId)));
                    modifySnippet(found ,updateRequest);
                    final Snippet saved = this.snippetRepository.save(found);

                    //Extract the metadata from the snippet, offloads to a separate thread to avoid blocking the main thread
                    if(found.getLanguage().equalsIgnoreCase(SnippetLanguage.JAVA.getLanguage())){
                        this.snippetParser.parseSnippetContent(saved);
                    }

                    return this.snippetMapper.toResponse(saved);
                }
        );
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
        final Snippet found = this.snippetRepository.findSnippetByAppUserAndId(user, snippetId)
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





}