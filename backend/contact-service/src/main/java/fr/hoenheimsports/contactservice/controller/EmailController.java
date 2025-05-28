package fr.hoenheimsports.contactservice.controller;

import fr.hoenheimsports.contactservice.dto.EmailRequest;
import fr.hoenheimsports.contactservice.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


/**
 * This controller handles email-related operations for the contact service.
 * It provides endpoints allowing users to send emails by submitting the required
 * information such as sender's name, email address, and message body.
 */
@RestController()
public class EmailController {
    private static final Logger logger = LoggerFactory.getLogger(EmailController.class);
    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Handles POST requests to send an email.
     *
     * @param emailRequest an {@link EmailRequest} object containing the sender's name,
     *                     email address, and message content. This parameter is validated
     *                     to ensure all required fields are present and properly formatted.
     *                     description = "This endpoint allows sending an email by providing the sender's name, email address, and message. Throws jakarta.validation.ConstraintViolationException if the provided emailRequest fails validation constraints."@throws org.springframework.http.converter.HttpMessageNotReadableException if the request
     *                     body is improperly formatted or missing.
     *                     <p>
     *                     The endpoint responds with a status of 204 (No Content) upon successful processing of
     *                     the email request, indicating that the email was sent successfully.
     */
    @PostMapping("/sendEmail")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Send an email",
            description = "This endpoint allows sending an email by providing the sender's name, email address, and message."
    )
    @ApiResponse(responseCode = "204", description = "Email successfully sent.")
    public void sendEmail(@Valid @RequestBody EmailRequest emailRequest) {
        logger.info("Reception de la requête de l'email. nom: {}", emailRequest.name());
        this.emailService.sendEmail(emailRequest);
    }
}
