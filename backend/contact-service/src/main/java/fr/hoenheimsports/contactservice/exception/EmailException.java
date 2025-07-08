package fr.hoenheimsports.contactservice.exception;

import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;

/**
 * Custom exception that represents errors occurring during the email sending process.
 * This exception is used to indicate and encapsulate issues related to email functionality.
 */
@Slf4j
public class EmailException extends RuntimeException {
    public EmailException(MessagingException e, String to, String subject, String message) {
        super("Erreur lors de l'envoi de l'e-mail à %s au sujet de %s avec le message suivant : %s".formatted(to, subject, message), e);
    }
}
