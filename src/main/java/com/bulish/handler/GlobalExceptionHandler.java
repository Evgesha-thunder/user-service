package com.bulish.handler;

import com.bulish.dto.ErrorResponse;
import com.bulish.exceptions.EmailAlreadyExistsException;
import com.bulish.exceptions.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorResponse handleValidationException(MethodArgumentNotValidException ex) {
        log.error("MethodArgumentNotValidException occurred: {}", ex.getMessage(), ex);

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        return constructErrorResponse("Validation failed", "Invalid request fields", errors);
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(UserNotFoundException ex) {
        log.error("UserNotFoundException occurred: {}", ex.getMessage(), ex);

        return constructErrorResponse("User not found", ex.getMessage(), null);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.error("HttpMessageNotReadableException occurred: {}", ex.getMessage(), ex);

        return constructErrorResponse("Bad JSON request", ex.getMessage(), null);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleEmailExistsException(EmailAlreadyExistsException ex) {
        log.error("EmailAlreadyExistsException occurred: {}", ex.getMessage(), ex);

        return constructErrorResponse("Email duplicate", ex.getMessage(), null );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleServerException(Exception ex) {
        log.error("Internal server error occurred: {}", ex.getMessage(), ex);

        return constructErrorResponse("Internal Server Error", ex.getMessage(), null);
    }

    private ErrorResponse constructErrorResponse(String title, String message, Map<String, String> errors) {
        return ErrorResponse.builder()
                .title(title)
                .message(message)
                .fieldErrors(errors)
                .timestamp(Instant.now())
                .build();
    }
}
