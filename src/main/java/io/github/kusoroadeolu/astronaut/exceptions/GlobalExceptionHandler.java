package io.github.kusoroadeolu.astronaut.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            NoSuchSnippetException.class
    })
    public ResponseEntity<ApiError> handleAuthExceptions(Exception e){
        ApiError error = new ApiError(403, e.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({
            NoSuchSnippetException.class
    })
    public ResponseEntity<ApiError> handleNotFoundExceptions(Exception e){
        ApiError error = new ApiError(404, e.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({
            SnippetParseException.class,
            SnippetPersistenceException.class,
            GistPersistenceException.class,
            IndexPersistenceException.class,
            SnippetComparisonException.class
    })
    public ResponseEntity<ApiError> handleServerSideExceptions(Exception e){
        ApiError error = new ApiError(500, e.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception e){
        ApiError error = new ApiError(500, "An unexpected error occurred", LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
