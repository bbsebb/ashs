package fr.hoenheimsports.trainingservice.controller;

import fr.hoenheimsports.trainingservice.dto.ApiErrorModel;
import fr.hoenheimsports.trainingservice.exception.TeamAlreadyExistsException;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Global exception handler for the application.
 * This class handles various types of exceptions that can occur during request processing,
 * providing appropriate responses with meaningful error descriptions and HTTP status codes.
 */
@RestControllerAdvice
public class GlobalControllerExceptionHandler {

    /**
     * Handles exceptions caused by validation errors on method arguments.
     *
     * @param ex      the exception that contains details about the validation error.
     * @param request the HTTP request that prompted the exception.
     * @return a {@link ResponseEntity} containing an {@link ApiErrorModel} describing
     * the validation error, along with a 400 (Bad Request) status code.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ApiResponse(responseCode = "400", description = "Data validation error\n")
    public ResponseEntity<ApiErrorModel> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String validationErrors = Stream.concat(
                        ex.getBindingResult().getFieldErrors().stream()
                                .map(error -> error.getField() + ": " + error.getDefaultMessage()),
                        ex.getBindingResult().getGlobalErrors().stream()
                                .map(error -> error.getObjectName() + ": " + error.getDefaultMessage())
                )
                .collect(Collectors.joining(", "));


        ApiErrorModel error = ApiErrorModel.createError(
                "https://www.hoenheimsports.fr/problem/validation-error",
                "Validation error in request payload",
                HttpStatus.BAD_REQUEST.value(),
                validationErrors,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }


    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ApiResponse(responseCode = "404", description = "Data validation error\n")
    public ResponseEntity<ApiErrorModel> handleValidationExceptions(EntityNotFoundException ex, HttpServletRequest request) {
        ApiErrorModel error = ApiErrorModel.createError(
                "https://www.hoenheimsports.fr/problem/validation-error",
                "Entity not found",
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(TeamAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ApiResponse(responseCode = "409", description = "Data conflict\n")
    public ResponseEntity<ApiErrorModel> handleValidationExceptions(TeamAlreadyExistsException ex, HttpServletRequest request) {
        ApiErrorModel error = ApiErrorModel.createError(
                "https://www.hoenheimsports.fr/problem/conflict",
                "Entity exists already",
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Handles exceptions caused by authorization denials.
     *
     * @param ex      the exception that indicates an authorization denied error.
     * @param request the HTTP request that resulted in the exception.
     * @return a {@link ResponseEntity} containing an {@link ApiErrorModel} describing
     * the error, along with a 403 (Forbidden) status code.
     */
    @ExceptionHandler(AuthorizationDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ApiResponse(responseCode = "403", description = "Authorization denied")
    public ResponseEntity<ApiErrorModel> handleAuthorizationDeniedException(AuthorizationDeniedException ex, HttpServletRequest request) {
        ApiErrorModel error = ApiErrorModel.createError(
                "https://www.hoenheimsports.fr/problem/authorization-denied",
                "Authorization denied for the requested operation",
                HttpStatus.FORBIDDEN.value(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ApiResponse(responseCode = "401", description = "Unauthorized access")
    public ResponseEntity<ApiErrorModel> handleAuthenticationException(AuthenticationException ex, HttpServletRequest request) {
        ApiErrorModel error = ApiErrorModel.createError(
                "https://www.hoenheimsports.fr/problem/unauthorized",
                "Authentication required or invalid credentials",
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }


    /**
     * Handles exceptions caused by runtime errors during request processing.
     *
     * @param ex      the runtime exception that occurred.
     * @param request the HTTP request that resulted in the exception.
     * @return a {@link ResponseEntity} containing an {@link ApiErrorModel} describing
     * the error, along with a 400 (Bad Request) status code.
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ApiResponse(responseCode = "400", description = "Invalid request")
    public ResponseEntity<ApiErrorModel> handleConversion(RuntimeException ex, HttpServletRequest request) {
        // Create an instance of ApiErrorModel with relevant information
        ApiErrorModel error = ApiErrorModel.createError(
                "https://www.hoenheimsports.fr/problem/runtime-exception", // Type du problème (URI hypothetique)
                "Error during request processing",               // Title
                HttpStatus.BAD_REQUEST.value(),                  // HTTP Code (400)
                ex.getMessage(),                                 // Error details (exception message)
                request.getRequestURI()                          // Identifier or error context
        );

        // Return a response with the body and HTTP code
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }


    /**
     * Handles all other unspecified exceptions that occur in the application.
     *
     * @param ex      the general exception that was not specifically handled by any other method.
     * @param request the HTTP request that caused the exception.
     * @return a {@link ResponseEntity} containing an {@link ApiErrorModel} describing
     * the internal server error, along with a 500 (Internal Server Error) status code.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<ApiErrorModel> handleGenericExceptions(Exception ex, HttpServletRequest request) {
        ApiErrorModel error = ApiErrorModel.createError(
                "https://www.hoenheimsports.fr/problem/internal-server-error",
                "An internal server error has occurred",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

}
