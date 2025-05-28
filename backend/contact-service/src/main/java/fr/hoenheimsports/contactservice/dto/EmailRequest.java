package fr.hoenheimsports.contactservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Represents the payload required for sending an email.
 * <p>
 * This record contains the following fields:
 * <ul>
 *     <li>{@code email}: The sender's email address. It must be a valid email and cannot be blank.</li>
 *     <li>{@code name}: The sender's name. It must have a length between 3 and 50 characters and cannot be blank.</li>
 *     <li>{@code message}: The content of the email. It must have a length between 10 and 1000 characters and cannot be blank.</li>
 * </ul>
 * Instances of this record are used as the request payload in the email sending endpoint.
 */
@Schema(description = "Represents the request payload for sending an email, including the sender's email address, name, and message.")
public record EmailRequest(
        @NotBlank(message = "The email field cannot be empty")
        @Email(message = "The email address is not valid")
        @Schema(description = "The sender's email address", example = "john.doe@example.com")
        String email,

        @NotBlank(message = "The name field cannot be empty")
        @Size(min = 3, max = 50, message = "The name must be between 3 and 50 characters")
        @Schema(description = "The sender's name", example = "John Doe")
        String name,

        @NotBlank(message = "The message field cannot be empty")
        @Size(min = 10, max = 1000, message = "The message must be between 10 and 1000 characters")
        @Schema(description = "The content of the email message", example = "Hello, I would like more information about your services.")
        String message) {
}

