package com.victor.astronaut.appuser.utils;


import com.victor.astronaut.exceptions.AppUserPersistenceException;
import com.victor.astronaut.exceptions.SnippetPersistenceException;
import com.victor.astronaut.utils.SupplierWithException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;

import static java.lang.String.format;

@Slf4j
public class AppUserUtils {
    public static <T>T handleWithException(String operation, Object resourceId, SupplierWithException<T> supplier){
        try {
            log.info("Attempting to {} app user: {}", operation, resourceId);
            T result = supplier.supply();
            log.info("Successfully {}d app user: {}", operation, resourceId);
            return result;
        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity error during app user {}: {}", operation, resourceId, e);
            throw new AppUserPersistenceException(
                    format("A data integrity error occurred while trying to %s: %s", operation, resourceId)
            );
        } catch (Exception e) {
            log.error("Unexpected error during app user {}: {}", operation, resourceId, e);
            throw new AppUserPersistenceException(
                    format("An unexpected error occurred while trying to %s: %s", operation, resourceId)
            );
        }
    }

}
