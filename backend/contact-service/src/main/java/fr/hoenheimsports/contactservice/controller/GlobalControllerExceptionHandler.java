package fr.hoenheimsports.contactservice.controller;

import fr.hoenheimsports.contactservice.dto.ApiErrorModel;
import fr.hoenheimsports.contactservice.exception.EmailException;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalControllerExceptionHandler {

    // Handling of 400 errors related to validation constraints
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ApiResponse(responseCode = "400", description = "Data validation error\n")
    public ResponseEntity<ApiErrorModel> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String validationErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
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

    // Handling of specific errors related to JavaMail (EmailException)
    @ExceptionHandler(EmailException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ApiResponse(responseCode = "500", description = "Error while sending an email")
    public ResponseEntity<ApiErrorModel> handleEmailExceptions(EmailException ex, HttpServletRequest request) {
        ApiErrorModel error = ApiErrorModel.createError(
                "https://www.hoenheimsports.fr/problem/email-error",
                "Internal error while sending the email",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    // Handling of other unspecified errors (500 Internal Server Error)
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
