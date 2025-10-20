package com.victor.astronaut.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppUserAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleConflictExceptions(Exception e){
        ApiError error = new ApiError(409, e.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({
            NoSuchSnippetException.class,
            NoSuchAppUserException.class
    })
    public ResponseEntity<ApiError> handleNotFoundExceptions(Exception e){
        ApiError error = new ApiError(404, e.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({
            AppUserPersistenceException.class,
            JwtException.class,
            SnippetParseException.class,
            SnippetPersistenceException.class
    })
    public ResponseEntity<ApiError> handleServerSideExceptions(Exception e){
        ApiError error = new ApiError(500, e.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiError> handleAuthenticationExceptions(Exception e){
        ApiError error = new ApiError(401, e.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception e){
        ApiError error = new ApiError(500, "An unexpected error occurred", LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }




}
