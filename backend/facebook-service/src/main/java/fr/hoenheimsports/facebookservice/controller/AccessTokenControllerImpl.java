package fr.hoenheimsports.facebookservice.controller;

import fr.hoenheimsports.facebookservice.controller.dto.AccessTokenDTORequest;
import fr.hoenheimsports.facebookservice.service.AccessTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Implementation of the AccessTokenController interface for handling Facebook access token operations.
 * 
 * <p>This controller provides REST API endpoints for managing Facebook access tokens,
 * specifically for exchanging short-lived tokens for long-lived ones. It processes
 * token exchange requests and delegates the actual token exchange to the AccessTokenService.</p>
 * 
 * @since 1.0
 */
@RestController
@RequestMapping("/api/token")
@Slf4j
public class AccessTokenControllerImpl implements AccessTokenController {

    private final AccessTokenService accessTokenService;

    public AccessTokenControllerImpl(AccessTokenService accessTokenService) {
        this.accessTokenService = accessTokenService;
    }

    /**
     * {@inheritDoc}
     * 
     * <p>This implementation delegates the token exchange to the AccessTokenService.
     * It logs the request and response for monitoring purposes and returns a 204 No Content
     * response upon successful token exchange.</p>
     * 
     * @param accessToken The request containing the short-lived token to exchange
     * @return A ResponseEntity with HTTP status 204 (No Content) if the token was exchanged successfully
     */
    @PostMapping("/exchange")
    @Override
    public ResponseEntity<Void> exchangeToken(@RequestBody AccessTokenDTORequest accessToken) {
        log.info("Réception d'une requête d'échange de token");
        log.debug("Échange d'un token court pour un token long");
        this.accessTokenService.exchangeToken(accessToken.accessToken());
        log.info("Échange de token effectué avec succès");
        return ResponseEntity.noContent().build();
    }

}
