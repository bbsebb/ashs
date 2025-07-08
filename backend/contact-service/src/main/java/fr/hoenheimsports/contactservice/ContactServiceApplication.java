package fr.hoenheimsports.contactservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Contact Service.
 * 
 * <p>This service handles contact form submissions and email sending functionality
 * for the Hoenheim Sports organization. It provides REST APIs for sending emails
 * from website visitors to the organization.</p>
 * 
 * @since 1.0
 */
@SpringBootApplication
public class ContactServiceApplication {

    /**
     * The main method that starts the Contact Service application.
     * 
     * @param args Command line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(ContactServiceApplication.class, args);
    }

}
