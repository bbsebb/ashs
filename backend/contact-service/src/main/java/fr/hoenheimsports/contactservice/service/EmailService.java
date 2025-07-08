package fr.hoenheimsports.contactservice.service;

import fr.hoenheimsports.contactservice.dto.EmailRequest;

/**
 * Service interface for sending emails.
 * 
 * <p>This interface defines the contract for sending emails based on the information
 * provided in an EmailRequest.</p>
 * 
 * @since 1.0
 */
public interface EmailService {

    /**
     * Sends an email using the information provided in the EmailRequest.
     * 
     * @param emailRequest The request containing the sender's email, name, and message
     */
    void sendEmail(EmailRequest emailRequest);
}
