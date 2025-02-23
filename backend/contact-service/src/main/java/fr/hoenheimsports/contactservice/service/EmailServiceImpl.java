package fr.hoenheimsports.contactservice.service;

import fr.hoenheimsports.contactservice.dto.EmailRequest;
import fr.hoenheimsports.contactservice.exception.EmailException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RefreshScope
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
    private final JavaMailSender javaMailSender;
    @Value("${custom.contact.email}")
    private String to;

    public EmailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void sendEmail(@NonNull EmailRequest emailRequest)   {
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

        logger.info("Préparation de l'envoi de l'e-mail. Destinataire: {}, Sujet: {}", to, name);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(warningMessage + message, false);
            javaMailSender.send(mimeMessage);
            logger.info("E-mail envoyé avec succès à {}", to);
        } catch (MessagingException e) {
            logger.error("Erreur lors de l'envoi de l'e-mail à {} : {}", to, e.getMessage(), e);
            throw new EmailException(e);
        }
    }
}