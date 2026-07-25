package com.pilotapi.exception;

import com.pilotapi.dto.ProblemDetailsDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetailsDto> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return buildProblem(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetailsDto> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String details = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(this::fieldErrorMessage)
            .collect(Collectors.joining("; "));
        return buildProblem(HttpStatus.BAD_REQUEST, details, request.getRequestURI());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetailsDto> handleBadRequest(IllegalArgumentException ex, HttpServletRequest request) {
        return buildProblem(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetailsDto> handleUnexpected(Exception ex, HttpServletRequest request) {
        LOGGER.error("Unhandled exception for {}", request.getRequestURI(), ex);
        return buildProblem(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error", request.getRequestURI());
    }

    private ResponseEntity<ProblemDetailsDto> buildProblem(HttpStatus status, String detail, String instance) {
        ProblemDetailsDto body = new ProblemDetailsDto();
        body.setType("about:blank");
        body.setTitle(status.getReasonPhrase());
        body.setStatus(status.value());
        body.setDetail(detail);
        body.setInstance(instance);
        return ResponseEntity.status(status).body(body);
    }

    private String fieldErrorMessage(FieldError error) {
        return error.getField() + ": " + error.getDefaultMessage();
    }
}
