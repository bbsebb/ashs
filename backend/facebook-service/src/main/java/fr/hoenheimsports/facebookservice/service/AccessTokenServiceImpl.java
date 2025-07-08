package fr.hoenheimsports.facebookservice.service;

import fr.hoenheimsports.facebookservice.feignClient.FacebookGraphAPIFeignClient;
import fr.hoenheimsports.facebookservice.mapper.AccessTokenMapper;
import fr.hoenheimsports.facebookservice.model.AccessToken;
import fr.hoenheimsports.facebookservice.repository.AccessTokenRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Implementation of the AccessTokenService interface for managing Facebook access tokens.
 * 
 * <p>This service is responsible for retrieving and exchanging Facebook access tokens
 * used to authenticate requests to the Facebook Graph API. It interacts with the
 * Facebook Graph API through a Feign client and stores tokens in a repository.</p>
 * 
 * @since 1.0
 */
@Service
@Slf4j
public class AccessTokenServiceImpl implements AccessTokenService {

    /**
     * Repository for storing and retrieving access tokens.
     */
    private final AccessTokenRepository accessTokenRepository;

    /**
     * Feign client for communicating with the Facebook Graph API.
     */
    private final FacebookGraphAPIFeignClient facebookGraphAPIFeignClient;

    /**
     * Mapper for converting between DTO and entity objects.
     */
    private final AccessTokenMapper accessTokenMapper;

    /**
     * The Facebook application client ID from configuration.
     */
    @Value("${facebook.api.client-id}")
    private String clientId;

    /**
     * The Facebook application client secret from configuration.
     */
    @Value("${facebook.api.client-secret}")
    private String clientSecret;

    /**
     * Constructs a new AccessTokenServiceImpl with the specified dependencies.
     * 
     * @param accessTokenRepository Repository for storing and retrieving access tokens
     * @param facebookGraphAPIFeignClient Feign client for communicating with the Facebook Graph API
     * @param accessTokenMapper Mapper for converting between DTO and entity objects
     */
    public AccessTokenServiceImpl(AccessTokenRepository accessTokenRepository,
                                  FacebookGraphAPIFeignClient facebookGraphAPIFeignClient, AccessTokenMapper accessTokenMapper) {
        this.accessTokenRepository = accessTokenRepository;
        this.facebookGraphAPIFeignClient = facebookGraphAPIFeignClient;
        this.accessTokenMapper = accessTokenMapper;
    }

    /**
     * {@inheritDoc}
     * 
     * <p>This implementation retrieves the token with ID 1 from the repository.
     * If no token is found, an EntityNotFoundException is thrown.</p>
     * 
     * @return The current access token entity
     * @throws EntityNotFoundException if no token is found in the repository
     */
    @Override
    public AccessToken getCurrentToken() {
        log.debug("Récupération du token d'accès courant");
        var token = accessTokenRepository.findById(1L).orElseThrow(EntityNotFoundException::new);
        log.debug("Token d'accès récupéré avec succès");
        return token;
    }

    /**
     * {@inheritDoc}
     * 
     * <p>This implementation calls the Facebook Graph API to exchange the token,
     * converts the response to an entity, and saves it in the repository with ID 1,
     * replacing any existing token.</p>
     * 
     * @param fbExchangeToken The short-lived token to exchange
     * @return The new long-lived access token entity
     */
    @Override
    public AccessToken exchangeToken(String fbExchangeToken) {
        log.info("Échange d'un token court pour un token long");
        log.debug("Appel à l'API Facebook pour échanger le token");
        var response = facebookGraphAPIFeignClient.exchangeToken(
                "fb_exchange_token",
                clientId,
                clientSecret,
                fbExchangeToken
        );
        log.debug("Réponse reçue de l'API Facebook, conversion en entité");
        var accessToken = this.accessTokenMapper.toEntity(response);
        accessToken.setId(1L);
        log.debug("Sauvegarde du nouveau token en base de données");
        var savedToken = accessTokenRepository.save(accessToken);
        log.info("Token échangé et sauvegardé avec succès, expiration: {}", savedToken.getExpireIn());
        return savedToken;
    }
}
