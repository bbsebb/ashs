package fr.hoenheimsports.contactservice.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmailRequestTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassValidationWithValidData() {
        // Prepare valid data
        EmailRequest emailRequest = new EmailRequest(
                "john.doe@example.com",
                "John Doe",
                "Hello, I would like more information about your services."
        );

        // Validate the object
        Set<ConstraintViolation<EmailRequest>> violations = validator.validate(emailRequest);

        // Verify that there are no violations
        assertTrue(violations.isEmpty(), "Il ne devrait pas y avoir de violations pour des données valides");
    }

    @Test
    void shouldFailValidationWithInvalidEmail() {
        // Prepare an invalid email
        EmailRequest emailRequest = new EmailRequest(
                "invalid-email",
                "John Doe",
                "Hello, this is a valid message."
        );

        // Valider l'objet
        Set<ConstraintViolation<EmailRequest>> violations = validator.validate(emailRequest);

        // Vérifier qu'une seule violation est présente
        assertEquals(1, violations.size(), "Il devrait y avoir une violation pour un email invalide");

        // Vérifier le message d'erreur
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("The email address is not valid")),
                "Le message d'erreur pour un email invalide doit être présent");
    }

    @Test
    void shouldFailValidationWithBlankEmail() {
        // Prepare an empty email
        EmailRequest emailRequest = new EmailRequest(
                "",
                "John Doe",
                "Hello, this is a valid message."
        );

        // Valider l'objet
        Set<ConstraintViolation<EmailRequest>> violations = validator.validate(emailRequest);

        // Vérifier qu'une seule violation est présente
        assertEquals(1, violations.size(), "Il devrait y avoir une violation pour un email vide");

        // Vérifier le message d'erreur
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("The email field cannot be empty")),
                "Le message d'erreur pour un email vide doit être présent");
    }

    @Test
    void shouldFailValidationWithBlankName() {
        // Prepare an empty name
        EmailRequest emailRequest = new EmailRequest(
                "john.doe@example.com",
                "",
                "Hello, this is a valid message."
        );

        // Valider l'objet
        Set<ConstraintViolation<EmailRequest>> violations = validator.validate(emailRequest);

        // Vérifier qu'il y a deux violations
        assertEquals(2, violations.size(), "Il devrait y avoir deux violations pour un nom vide");

        // Verify the error messages
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("The name field cannot be empty")),
                "Le message d'erreur pour un nom vide doit être présent");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("The name must be between 3 and 50 characters")),
                "Le message d'erreur pour une taille de nom incorrecte doit être présent");
    }

    @Test
    void shouldFailValidationWithNameTooShort() {
        // Prepare a name that is too short
        EmailRequest emailRequest = new EmailRequest(
                "john.doe@example.com",
                "Jo",
                "Hello, this is a valid message."
        );

        // Valider l'objet
        Set<ConstraintViolation<EmailRequest>> violations = validator.validate(emailRequest);

        // Vérifier qu'une seule violation est présente
        assertEquals(1, violations.size(), "Il devrait y avoir une violation pour un nom trop court");

        // Vérifier le message d'erreur
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("The name must be between 3 and 50 characters")),
                "Le message d'erreur pour un nom trop court doit être présent");
    }

    @Test
    void shouldFailValidationWithNameTooLong() {
        // Prepare a name that is too long
        String longName = "A".repeat(51); // Nom de 51 caractères
        EmailRequest emailRequest = new EmailRequest(
                "john.doe@example.com",
                longName,
                "Hello, this is a valid message."
        );

        // Valider l'objet
        Set<ConstraintViolation<EmailRequest>> violations = validator.validate(emailRequest);

        // Vérifier qu'une seule violation est présente
        assertEquals(1, violations.size(), "Il devrait y avoir une violation pour un nom trop long");

        // Vérifier le message d'erreur
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("The name must be between 3 and 50 characters")),
                "Le message d'erreur pour un nom trop long doit être présent");
    }

    @Test
    void shouldFailValidationWithBlankMessage() {
        // Prepare an empty message
        EmailRequest emailRequest = new EmailRequest(
                "john.doe@example.com",
                "John Doe",
                ""
        );

        // Valider l'objet
        Set<ConstraintViolation<EmailRequest>> violations = validator.validate(emailRequest);

        // Verify that there are two violations
        assertEquals(2, violations.size(), "Il devrait y avoir deux violations pour un message vide");

        // Verify the associated error messages
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("The message field cannot be empty")),
                "Le message d'erreur pour un message vide doit être présent");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("The message must be between 10 and 1000 characters")),
                "Le message d'erreur pour une taille de message incorrecte doit être présent");
    }

    @Test
    void shouldFailValidationWithShortMessage() {
        // Prepare a message that is too short
        EmailRequest emailRequest = new EmailRequest(
                "john.doe@example.com",
                "John Doe",
                "Short"
        );

        // Valider l'objet
        Set<ConstraintViolation<EmailRequest>> violations = validator.validate(emailRequest);

        // Verify that only one violation is present
        assertEquals(1, violations.size(), "Il devrait y avoir une violation pour un message trop court");

        // Verify the error message
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("The message must be between 10 and 1000 characters")),
                "Le message d'erreur pour un message trop court doit être présent");
    }

    @Test
    void shouldFailValidationWithLongMessage() {
        // Prepare a message that is too long
        String longMessage = "A".repeat(1001); // Message de 1001 caractères
        EmailRequest emailRequest = new EmailRequest(
                "john.doe@example.com",
                "John Doe",
                longMessage
        );

        // Valider l'objet
        Set<ConstraintViolation<EmailRequest>> violations = validator.validate(emailRequest);

        // Vérifier qu'une seule violation est présente
        assertEquals(1, violations.size(), "Il devrait y avoir une violation pour un message trop long");

        // Vérifier le message d'erreur
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("The message must be between 10 and 1000 characters")),
                "Le message d'erreur pour un message trop long doit être présent");
    }

    @Test
    void shouldFailValidationWithMultipleErrors() {
        // Prepare an EmailRequest with multiple simultaneous errors
        EmailRequest emailRequest = new EmailRequest(
                "", // Email vide
                "Jo", // Nom trop court
                "Short" // Message trop court
        );

        // Validate the object using the validator
        Set<ConstraintViolation<EmailRequest>> violations = validator.validate(emailRequest);

        // Verify that multiple violations are present
        assertEquals(3, violations.size(), "Il devrait y avoir 3 violations de contraintes");

        // Verify the messages associated with the violations
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("The email field cannot be empty")),
                "Le message d'erreur pour l'email vide doit être présent");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("The name must be between 3 and 50 characters")),
                "Le message d'erreur pour le nom trop court doit être présent");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("The message must be between 10 and 1000 characters")),
                "Le message d'erreur pour le message trop court doit être présent");
    }
}