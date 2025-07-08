package fr.hoenheimsports.trainingservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http

                .csrf(AbstractHttpConfigurer::disable) // Désactiver CSRF avec l'API moderne
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() //preflight request
                        .requestMatchers(HttpMethod.GET).permitAll()
                        .anyRequest().authenticated() // Autoriser toutes les requêtes sans restriction
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(AbstractHttpConfigurer::disable) // Désactiver l'authentification HTTP Basic
                .formLogin(AbstractHttpConfigurer::disable)
                .oauth2ResourceServer(resourceServer -> resourceServer.jwt(jwtDecoder -> jwtDecoder.jwtAuthenticationConverter(new SimpleKeycloakJwtAuthenticationConverter()))); // Désactiver le formulaire de connexion

        return http.build();

    }
}
