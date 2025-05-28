package fr.hoenheimsports.contactservice.service;

import fr.hoenheimsports.contactservice.dto.EmailRequest;

public interface EmailService {
    void sendEmail(EmailRequest emailRequest)  ;
}
