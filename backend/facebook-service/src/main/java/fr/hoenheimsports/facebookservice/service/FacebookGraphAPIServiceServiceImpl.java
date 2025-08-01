package fr.hoenheimsports.facebookservice.service;

import fr.hoenheimsports.facebookservice.feignClient.FacebookGraphAPIFeignClient;
import fr.hoenheimsports.facebookservice.feignClient.dto.GraphApiResponse;
import fr.hoenheimsports.facebookservice.mapper.FeedEntityMapper;
import fr.hoenheimsports.facebookservice.model.AccessToken;
import fr.hoenheimsports.facebookservice.model.FeedEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Implementation of the FacebookGraphAPIService interface for interacting with the Facebook Graph API.
 *
 * <p>This service is responsible for fetching feed posts from the organization's Facebook page
 * using the Facebook Graph API. It handles authentication, data retrieval, and conversion
 * of API responses to domain entities.</p>
 *
 * <p>The results of the fetchFacebookFeeds method are cached to improve performance
 * and reduce the number of calls to the Facebook API.</p>
 *
 * @since 1.0
 */
@Service
@Slf4j
public class FacebookGraphAPIServiceServiceImpl implements FacebookGraphAPIService {
    /**
     * Feign client for communicating with the Facebook Graph API.
     */
    private final FacebookGraphAPIFeignClient facebookGraphAPIFeignClient;

    /**
     * Mapper for converting between API response DTOs and domain entities.
     */
    private final FeedEntityMapper feedEntityMapper;

    /**
     * Service for managing Facebook access tokens.
     */
    private final AccessTokenService accessTokenService;

    /**
     * Constructs a new FacebookGraphAPIServiceServiceImpl with the specified dependencies.
     *
     * @param facebookGraphAPIFeignClient Feign client for communicating with the Facebook Graph API
     * @param feedEntityMapper            Mapper for converting between API response DTOs and domain entities
     * @param accessTokenService          Service for managing Facebook access tokens
     */
    public FacebookGraphAPIServiceServiceImpl(
            FacebookGraphAPIFeignClient facebookGraphAPIFeignClient,
            FeedEntityMapper feedEntityMapper,
            AccessTokenService accessTokenService) {
        this.facebookGraphAPIFeignClient = facebookGraphAPIFeignClient;
        this.feedEntityMapper = feedEntityMapper;
        this.accessTokenService = accessTokenService;
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation retrieves the current access token, exchanges it to ensure
     * its validity, then calls the Facebook Graph API to fetch feed posts. The results
     * are cached to improve performance.</p>
     *
     * <p>The method requests specific fields from the API including post ID, creation time,
     * message content, and attachments with their media information.</p>
     *
     * @return A list of feed entities containing the posts and their attachments, or an empty list if no data is available
     */
    @Cacheable(value = "facebookFeeds", unless = "#result.isEmpty()")
    @Override
    public List<FeedEntity> fetchFacebookFeeds() {
        log.info("Récupération des feeds Facebook");
        log.debug("Récupération du token d'accès courant");

        return accessTokenService.getCurrentToken()
                .map(this::fetchAndMapFeeds)
                .orElseGet(List::of);
    }

    private List<FeedEntity> fetchAndMapFeeds(AccessToken token) {
        exchangeAndValidateToken(token);

        var fields = encodeRequestedFields();
        var apiGraph = getFeedsFromFacebook(fields, token.getAccessToken());

        if (apiGraph == null || apiGraph.data() == null) {
            log.warn("Aucune donnée reçue de l'API Facebook");
            return List.of();
        }

        return mapApiGraphToFeedEntities(apiGraph);
    }

    private void exchangeAndValidateToken(AccessToken token) {
        log.debug("Échange du token pour assurer sa validité");
        this.accessTokenService.exchangeToken(token.getAccessToken());
    }

    private String encodeRequestedFields() {
        log.debug("Préparation des paramètres pour l'appel à l'API Facebook");
        return URLEncoder.encode(
                "id,created_time,message,attachments.limit(100){media_type,media,subattachments,type}",
                StandardCharsets.UTF_8
        );
    }

    private GraphApiResponse getFeedsFromFacebook(String fields, String accessToken) {
        log.debug("Appel à l'API Facebook pour récupérer les feeds");
        return this.facebookGraphAPIFeignClient.getFeed(fields, 100, accessToken);
    }

    private List<FeedEntity> mapApiGraphToFeedEntities(GraphApiResponse apiGraph) {
        log.debug("Conversion des données reçues en entités");
        var feeds = apiGraph.data().stream().map(this.feedEntityMapper::toEntity).toList();
        log.info("{} feeds Facebook récupérés", feeds.size());
        return feeds;
    }

}
