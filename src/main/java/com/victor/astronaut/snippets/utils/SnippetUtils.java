package com.victor.astronaut.snippets.utils;

import com.victor.astronaut.appuser.AppUser;
import com.victor.astronaut.exceptions.SnippetPersistenceException;
import com.victor.astronaut.snippets.Snippet;
import com.victor.astronaut.snippets.dto.SnippetCreationRequest;
import com.victor.astronaut.snippets.dto.SnippetUpdateRequest;
import com.victor.astronaut.utils.SupplierWithException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;

import static java.lang.String.format;

@Slf4j
public class SnippetUtils{

    //Utility method that abstracts the logging and error handling when dealing with CRUD snippets
    public static <T> T executeWithException(String operation, Object resourceId, SupplierWithException<T> supplier) {
        try {
            log.info("Attempting to {} snippet: {}", operation, resourceId);
            T result = supplier.supply();
            log.info("Successfully {}d snippet: {}", operation, resourceId);
            return result;
        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity error during snippet {}: {}", operation, resourceId, e);
            throw new SnippetPersistenceException(
                    format("A data integrity error occurred while trying to %s: %s", operation, resourceId)
            );
        } catch (Exception e) {
            log.error("Unexpected error during snippet {}: {}", operation, resourceId, e);
            throw new SnippetPersistenceException(
                    format("An unexpected error occurred while trying to %s: %s", operation, resourceId)
            );
        }
    }

    //Utility method to build a new snippet
    public static Snippet buildSnippet(AppUser appUser, SnippetCreationRequest creationRequest){
        return Snippet
                .builder()
                .name(creationRequest.snippetName())
                .tags(creationRequest.tags())
                .language(creationRequest.language())
                .isDraft(true)
                .appUser(appUser)
                .build();
    }


    //Utility method to modify an already existing snippet
    public static void modifySnippet(Snippet snippet, SnippetUpdateRequest updateRequest){
        String content = updateRequest.content();
        boolean isDraft = content == null || content.isBlank();
        snippet.setContent(content);
        snippet.setName(updateRequest.snippetName());
        snippet.setTags(updateRequest.tags());
        snippet.setDraft(isDraft);
        snippet.setExtraNotes(updateRequest.extraNotes());
    }


}
