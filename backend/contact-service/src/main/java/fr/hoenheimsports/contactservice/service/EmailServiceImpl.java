package fr.hoenheimsports.contactservice.service;

import fr.hoenheimsports.contactservice.dto.EmailRequest;
import fr.hoenheimsports.contactservice.exception.EmailException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Implementation of the EmailService interface responsible for sending emails.
 * 
 * <p>This service uses the JavaMailSender for email composition and delivery.
 * It adds a security warning message in the email body to notify the recipient 
 * about the sender's identity for security purposes.</p>
 * 
 * @since 1.0
 */
@Service
@RefreshScope
@Slf4j
public class EmailServiceImpl implements EmailService {


    /**
     * The mail sender service used to send emails.
     */
    private final JavaMailSender javaMailSender;

    /**
     * The recipient email address configured in the application properties.
     */
    @Value("${custom.contact.email}")
    private String to;

    /**
     * Constructs a new EmailServiceImpl with the specified JavaMailSender.
     * 
     * @param javaMailSender The mail sender service to use for sending emails
     */
    public EmailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
        log.debug("Initialisation du service d'envoi d'emails avec JavaMailSender");
    }

    /**
     * Sends an email using the details provided in the {@link EmailRequest}.
     * The email includes a security warning message in the body and is sent
     * to the configured default recipient email address.
     *
     * @param emailRequest An {@link EmailRequest} object containing the sender's name,
     *                     email address, and the message content to be sent.
     *                     The {@link EmailRequest} must not be null.
     * @throws EmailException If an error occurs while sending the email using {@link JavaMailSender}.
     */
    @Override
    public void sendEmail(@NonNull EmailRequest emailRequest) {
        log.info("Traitement d'une demande d'envoi d'email de {} <{}>", emailRequest.name(), emailRequest.email());
        log.debug("Contenu du message: longueur={} caractères", emailRequest.message().length());

        var name = emailRequest.name();
        var from = emailRequest.email();
        var message = emailRequest.message();
        var warningMessage = String.format(
                """
                        Attention : ce message a été écrit par %s (%s).
                        Merci de ne pas cliquer sur 'Répondre' pour répondre directement à cet email.

                        """,
                name, from
        );
        var subject = "Notification de formulaire de contact de : " + name;

        log.debug("Création du message avec avertissement de sécurité");
        log.info("Préparation de l'envoi de l'e-mail à {}", to);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            log.debug("Configuration du message: destinataire={}, sujet={}", to, subject);
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(warningMessage + message, false);

            log.debug("Envoi du message via JavaMailSender");
            javaMailSender.send(mimeMessage);
            log.info("E-mail envoyé avec succès à {}", to);
        } catch (MessagingException e) {
            log.error("Erreur lors de l'envoi de l'e-mail à {} : {}", to, e.getMessage(), e);
            throw new EmailException(e, to, subject, message);
        }
    }
}
