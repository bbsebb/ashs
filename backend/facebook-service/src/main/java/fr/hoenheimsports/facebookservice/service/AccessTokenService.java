package fr.hoenheimsports.facebookservice.service;

import fr.hoenheimsports.facebookservice.model.AccessToken;

import java.util.Optional;

/**
 * Service interface for managing Facebook access tokens.
 *
 * <p>This interface defines the contract for retrieving and exchanging
 * Facebook access tokens used to authenticate requests to the Facebook Graph API.</p>
 *
 * @since 1.0
 */
public interface AccessTokenService {

    /**
     * Retrieves the current Facebook access token.
     *
     * <p>This method returns the most recent valid access token stored in the system.
     * The token is used to authenticate requests to the Facebook Graph API.</p>
     *
     * @return The current access token entity
     */
    Optional<AccessToken> getCurrentToken();

    /**
     * Exchanges a short-lived token for a long-lived token.
     *
     * <p>This method takes a short-lived token obtained from Facebook and exchanges it
     * for a long-lived token with extended expiration time. The new token is stored
     * in the system and returned.</p>
     *
     * @param fbExchangeToken The short-lived token to exchange
     * @return The new long-lived access token entity
     */
    AccessToken exchangeToken(String fbExchangeToken);
}
