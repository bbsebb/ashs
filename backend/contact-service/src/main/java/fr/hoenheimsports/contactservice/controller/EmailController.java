package fr.hoenheimsports.contactservice.controller;

import fr.hoenheimsports.contactservice.dto.EmailRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Controller interface defining the REST API endpoints for email operations.
 * 
 * <p>This interface provides endpoints for sending emails from website visitors
 * to the organization. It defines the contract that implementing classes must follow.</p>
 * 
 * @since 1.0
 */
public interface EmailController {
    /**
     * Sends an email using the information provided in the request.
     * 
     * <p>This endpoint accepts an EmailRequest containing the sender's information
     * and message content, then forwards it to the appropriate service for processing.
     * No content is returned upon successful completion.</p>
     * 
     * @param emailRequest The request containing the sender's email, name, and message
     * @return A ResponseEntity with HTTP status 204 (No Content) if the email was sent successfully
     */
    @PostMapping("/sendEmail")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Send an email",
            description = "This endpoint allows sending an email by providing the sender's name, email address, and message."
    )
    @ApiResponse(responseCode = "204", description = "Email successfully sent.")
    ResponseEntity<Void> sendEmail(@Valid @RequestBody EmailRequest emailRequest);
}
