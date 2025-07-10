package fr.hoenheimsports.contactservice.controller;

import fr.hoenheimsports.contactservice.dto.EmailRequest;
import fr.hoenheimsports.contactservice.dto.RootResponse;
import fr.hoenheimsports.contactservice.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.afford;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


/**
 * Implementation of the EmailController interface for handling email operations.
 *
 * <p>This controller provides REST API endpoints for sending emails from website visitors
 * to the organization. It processes the email requests and delegates the actual email
 * sending to the EmailService.</p>
 *
 * @since 1.0
 */
@RestController()
@RequestMapping("/api")
@Slf4j
public class EmailControllerImpl implements EmailController {
    /**
     * The email service used to send emails.
     */
    private final EmailService emailService;

    /**
     * Constructs a new EmailControllerImpl with the specified EmailService.
     *
     * @param emailService The service to use for sending emails
     */
    public EmailControllerImpl(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Sends an email using the information provided in the request.
     *
     * <p>This endpoint accepts an EmailRequest containing the sender's information
     * and message content, then forwards it to the EmailService for processing.
     * No content is returned upon successful completion.</p>
     *
     * <p>The request is validated to ensure all required fields are present and properly formatted.
     * If validation fails, a ConstraintViolationException is thrown.</p>
     *
     * @param emailRequest The request containing the sender's email, name, and message
     * @return A ResponseEntity with HTTP status 204 (No Content) if the email was sent successfully
     * @throws jakarta.validation.ConstraintViolationException                    if the provided request fails validation
     * @throws org.springframework.http.converter.HttpMessageNotReadableException if the request body is improperly formatted or missing
     */
    @PostMapping("/sendEmail")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Send an email",
            description = "This endpoint allows sending an email by providing the sender's name, email address, and message."
    )
    @ApiResponse(responseCode = "204", description = "Email successfully sent.")
    @Override
    public ResponseEntity<Void> sendEmail(@Valid @RequestBody EmailRequest emailRequest) {
        log.info("Réception d'une requête d'envoi d'email de {} <{}>", emailRequest.name(), emailRequest.email());
        log.debug("Contenu du message: {}", emailRequest.message());
        this.emailService.sendEmail(emailRequest);
        log.info("Traitement de la requête d'email terminé avec succès");
        return ResponseEntity.noContent().build();
    }

    @GetMapping()
    public ResponseEntity<EntityModel<RootResponse>> getRoot() {
        var entity = EntityModel.of(new RootResponse());
        entity.add(Link.of("/api").withSelfRel().andAffordances(
                List.of(
                        afford(methodOn(EmailControllerImpl.class).sendEmail(new EmailRequest("test@test.fr", "test", "test"))), afford(methodOn(EmailControllerImpl.class).sendEmail(new EmailRequest("test@test.fr", "test", "test")))
                )));

        return ResponseEntity.ok(entity);
    }


}
