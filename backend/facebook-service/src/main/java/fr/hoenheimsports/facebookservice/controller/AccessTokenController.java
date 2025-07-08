package fr.hoenheimsports.facebookservice.controller;

import fr.hoenheimsports.facebookservice.controller.dto.AccessTokenDTORequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Controller interface defining the REST API endpoints for Facebook access token operations.
 * 
 * <p>This interface provides endpoints for managing Facebook access tokens,
 * specifically for exchanging short-lived tokens for long-lived ones.</p>
 * 
 * @since 1.0
 */
public interface AccessTokenController {
    /**
     * Exchanges a short-lived Facebook token for a long-lived token.
     * 
     * <p>This endpoint accepts a request containing a short-lived token and exchanges it
     * for a long-lived token with extended expiration time. The new token is stored
     * in the system for future use.</p>
     * 
     * @param accessToken The request containing the short-lived token to exchange
     * @return A ResponseEntity with HTTP status 204 (No Content) if the token was exchanged successfully
     */
    @PostMapping("/exchange")
    ResponseEntity<Void> exchangeToken(@RequestBody AccessTokenDTORequest accessToken);
}
