
package com.cmsc495.assetmanagement.exception;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

/**
 * Provides centralized and consistent API error responses.
 *
 * Internal implementation details and database errors
 * are not exposed to application users.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger =
            LoggerFactory.getLogger(
                    GlobalExceptionHandler.class);

    // Handle request DTO validation failures

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request) {

        Map<String, String> errors =
                new LinkedHashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.putIfAbsent(
                                error.getField(),
                                error.getDefaultMessage() == null
                                        ? "Invalid value"
                                        : error.getDefaultMessage()));

        return buildError(
                HttpStatus.BAD_REQUEST,
                "Request validation failed",
                request,
                errors);
    }

    // Handle constraint validation failures

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError>
            handleConstraintViolation(

            ConstraintViolationException exception,
            HttpServletRequest request) {

        Map<String, String> errors =
                new LinkedHashMap<>();

        exception.getConstraintViolations()
                .forEach(violation ->
                        errors.put(
                                violation.getPropertyPath()
                                        .toString(),
                                violation.getMessage()));

        return buildError(
                HttpStatus.BAD_REQUEST,
                "Input validation failed",
                request,
                errors);
    }

    // Handle malformed JSON and unsupported enum values

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleInvalidJson(
            HttpMessageNotReadableException exception,
            HttpServletRequest request) {

        return buildError(
                HttpStatus.BAD_REQUEST,
                "Invalid request body or unsupported field value",
                request,
                Map.of());
    }

    // Handle invalid URL and query parameter values

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(
            MethodArgumentTypeMismatchException exception,
            HttpServletRequest request) {

        return buildError(
                HttpStatus.BAD_REQUEST,
                "Invalid value for parameter: "
                        + exception.getName(),
                request,
                Map.of());
    }

    // Handle missing required request parameters

    @ExceptionHandler(
            MissingServletRequestParameterException.class)
    public ResponseEntity<ApiError> handleMissingParameter(
            MissingServletRequestParameterException exception,
            HttpServletRequest request) {

        return buildError(
                HttpStatus.BAD_REQUEST,
                "Missing required parameter: "
                        + exception.getParameterName(),
                request,
                Map.of());
    }

    // Handle business logic and resource errors

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleResponseStatus(
            ResponseStatusException exception,
            HttpServletRequest request) {

        String message = exception.getReason();

        if (message == null || message.isBlank()) {
            message = "The request could not be completed";
        }

        return buildError(
                exception.getStatusCode(),
                message,
                request,
                Map.of());
    }

    // Handle database constraint conflicts

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrity(
            DataIntegrityViolationException exception,
            HttpServletRequest request) {

        logger.warn(
                "Database integrity constraint violation");

        return buildError(
                HttpStatus.CONFLICT,
                "The operation conflicts with existing data or database constraints",
                request,
                Map.of());
    }

    // Handle access denied within controller execution

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(
            AccessDeniedException exception,
            HttpServletRequest request) {

        return buildError(
                HttpStatus.FORBIDDEN,
                "You do not have permission to perform this operation",
                request,
                Map.of());
    }

    // Construct a standardized error response

    private ResponseEntity<ApiError> buildError(
            HttpStatusCode status,
            String message,
            HttpServletRequest request,
            Map<String, String> validationErrors) {

        HttpStatus resolvedStatus =
                HttpStatus.resolve(status.value());

        String error = resolvedStatus == null
                ? "Request Error"
                : resolvedStatus.getReasonPhrase();

        ApiError response = new ApiError(
                Instant.now().toString(),
                status.value(),
                error,
                message,
                request.getRequestURI(),
                validationErrors);

        return ResponseEntity
                .status(status)
                .body(response);
    }

    // Standard API error response structure

    public record ApiError(
            String timestamp,
            int status,
            String error,
            String message,
            String path,
            Map<String, String> validationErrors) {
    }
}
