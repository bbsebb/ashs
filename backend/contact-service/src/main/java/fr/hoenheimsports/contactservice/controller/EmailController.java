package fr.hoenheimsports.contactservice.controller;

import fr.hoenheimsports.contactservice.dto.EmailRequest;
import fr.hoenheimsports.contactservice.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;


/**
 * This controller handles email-related operations for the contact service.
 * It provides endpoints allowing users to send emails by submitting the required
 * information such as sender's name, email address, and message body.
 */
@RestController()
@RequestMapping("/api")
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
    public ResponseEntity<Void> sendEmail(@Valid @RequestBody EmailRequest emailRequest) {
        logger.info("Reception de la requête de l'email. nom: {}", emailRequest.name());
        this.emailService.sendEmail(emailRequest);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("")
    @Operation(
            summary = "Get the root endpoint",
            description = "This endpoint returns the root endpoint of the contact service."
    )
    @ApiResponse(responseCode = "200", description = "The root endpoint of the contact service.")
    public ResponseEntity<RepresentationModel<?>> getRootEndpoint() {
        RepresentationModel<?> root = new RepresentationModel<>();
        Link selfLink = linkTo(methodOn(EmailController.class).getRootEndpoint()).withSelfRel();
        var emailRequest = new EmailRequest("affordance@hoenheimsports.fr", "<EMAIL>", "affordance");
        selfLink = selfLink.andAffordances(List.of(
                        afford(methodOn(EmailController.class).sendEmail(emailRequest)), //default name
                        afford(methodOn(EmailController.class).sendEmail(emailRequest))
                )
        );
        root.add(selfLink);
        return ResponseEntity.ok(root);
    }


}
