package dev.sorokin.eventmanager.web;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorMessageResponse> handleEntityNotFoundException(
            EntityNotFoundException e
    ) {
        log.error("Got entity not found exception={}", e.getMessage());
        var errorMessage = new ErrorMessageResponse(
                "Entity not found",
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessageResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e
    ) {
        log.error("Got validation exception={}", e.getMessage());
        String detailedMessage = constructMethodArgumentNotValidMessage(e);
        var errorMessage = new ErrorMessageResponse(
                "Request validation error",
                detailedMessage,
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorMessageResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e
    ) {
        log.error("Got message not readable exception={}", e.getMessage());
        var errorMessage = new ErrorMessageResponse(
                "Request body is not readable",
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessageResponse> handleException(
            Exception e
    ) {
        log.error("Got server error={}", e.getMessage());
        var errorMessage = new ErrorMessageResponse(
                "Server error",
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
    }

    private static String constructMethodArgumentNotValidMessage(
            MethodArgumentNotValidException e) {
        return e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
    }
}
