package com.pilotapi.exception;

import com.pilotapi.common.RequestSettings;
import com.pilotapi.dto.ProblemDetailsDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetailsDto> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return buildProblem(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetailsDto> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String details = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(this::fieldErrorMessage)
            .collect(Collectors.joining("; "));
        return buildProblem(HttpStatus.BAD_REQUEST, details, request);
    }

    @ExceptionHandler({
        MethodArgumentTypeMismatchException.class,
        MissingServletRequestParameterException.class,
        HttpMessageNotReadableException.class
    })
    public ResponseEntity<ProblemDetailsDto> handleMalformedRequest(Exception ex, HttpServletRequest request) {
        return buildProblem(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetailsDto> handleBadRequest(IllegalArgumentException ex, HttpServletRequest request) {
        return buildProblem(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetailsDto> handleUnexpected(Exception ex, HttpServletRequest request) {
        LOGGER.error("Unhandled exception for {}", request.getRequestURI(), ex);
        return buildProblem(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error", request);
    }

    private ResponseEntity<ProblemDetailsDto> buildProblem(HttpStatus status, String detail, HttpServletRequest request) {
        RequestSettings requestSettings = RequestSettings.from(request);
        ProblemDetailsDto body = new ProblemDetailsDto();
        body.setType("about:blank");
        body.setTitle(status.getReasonPhrase());
        body.setStatus(status.value());
        body.setDetail(detail);
        body.setInstance(request.getRequestURI());
        body.addExtension("correlationId", requestSettings.getCorrelationId());
        body.addExtension("requestId", requestSettings.getRequestId());
        body.addExtension("timestamp", Instant.now());
        return ResponseEntity.status(status).body(body);
    }

    private String fieldErrorMessage(FieldError error) {
        return error.getField() + ": " + error.getDefaultMessage();
    }
}
