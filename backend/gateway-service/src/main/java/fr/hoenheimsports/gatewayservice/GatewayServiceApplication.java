package fr.hoenheimsports.gatewayservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GatewayServiceApplication {
    private static final Logger logger = LoggerFactory.getLogger(GatewayServiceApplication.class);

    public static void main(String[] args) {
        logger.info("Démarrage de l'application Gateway Service");
        SpringApplication.run(GatewayServiceApplication.class, args);
        logger.info("Application Gateway Service démarrée avec succès");
    }

}
