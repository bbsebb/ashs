package fr.hoenheimsports.trainingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Training Service.
 * 
 * <p>This service manages teams, coaches, training sessions, and related entities
 * for the Hoenheim Sports organization. It provides REST APIs for creating, reading,
 * updating, and deleting training-related data.</p>
 * 
 * @since 1.0
 */
@SpringBootApplication
public class TrainingServiceApplication {

    /**
     * The main method that starts the Training Service application.
     * 
     * @param args Command line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(TrainingServiceApplication.class, args);
    }

}
