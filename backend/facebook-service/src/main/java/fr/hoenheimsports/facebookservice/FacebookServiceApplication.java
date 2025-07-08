package fr.hoenheimsports.facebookservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Main application class for the Facebook Service.
 * 
 * <p>This service is responsible for fetching and managing Facebook feed data
 * from the Hoenheim Sports organization's Facebook page. It provides REST APIs
 * for retrieving posts, attachments, and media from the Facebook Graph API.</p>
 * 
 * <p>The service uses Feign clients to communicate with the Facebook Graph API.</p>
 * 
 * @since 1.0
 */
@EnableFeignClients
@SpringBootApplication
public class FacebookServiceApplication {

    /**
     * The main method that starts the Facebook Service application.
     * 
     * @param args Command line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(FacebookServiceApplication.class, args);
    }

}
