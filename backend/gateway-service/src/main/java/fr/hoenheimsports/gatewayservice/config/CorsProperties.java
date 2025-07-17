package fr.hoenheimsports.gatewayservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "custom.cors")
public record CorsProperties(List<String> allowedOrigins) {
}
