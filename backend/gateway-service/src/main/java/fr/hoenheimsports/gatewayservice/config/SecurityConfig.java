package fr.hoenheimsports.gatewayservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableConfigurationProperties(CorsProperties.class)
public class SecurityConfig {
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    private final CorsProperties corsProperties;

    public SecurityConfig(CorsProperties corsProperties) {
        this.corsProperties = corsProperties;
        logger.debug("Initialisation de la configuration de sécurité");
    }

    @Bean
    public CorsWebFilter corsWebFilter() {
        logger.debug("Configuration du filtre CORS");
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(corsProperties.allowedOrigins());
        corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfig.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        corsConfig.setAllowCredentials(true);
        logger.debug("Origines autorisées: {}", corsConfig.getAllowedOrigins());
        logger.debug("Méthodes autorisées: {}", corsConfig.getAllowedMethods());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig); // ou /api/** si tu veux cibler seulement tes endpoints REST locaux
        logger.debug("Configuration CORS enregistrée pour le pattern: /**");

        return new CorsWebFilter(source);
    }
}
