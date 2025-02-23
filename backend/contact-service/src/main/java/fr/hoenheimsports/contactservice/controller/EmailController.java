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


@RestController()
public class EmailController {
    private static final Logger logger = LoggerFactory.getLogger(EmailController.class);
    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

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
