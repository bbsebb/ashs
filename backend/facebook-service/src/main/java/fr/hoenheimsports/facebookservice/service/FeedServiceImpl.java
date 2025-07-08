package fr.hoenheimsports.facebookservice.service;

import fr.hoenheimsports.facebookservice.model.FeedEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of the FeedService interface for retrieving Facebook feed posts.
 * 
 * <p>This service is responsible for retrieving Facebook feed posts from the
 * FacebookGraphAPIService and providing them to clients, either as a complete
 * list or as a paginated subset.</p>
 * 
 * @since 1.0
 */
@Service
@Slf4j
public class FeedServiceImpl implements FeedService {
    /**
     * Service for interacting with the Facebook Graph API.
     */
    private final FacebookGraphAPIService facebookGraphAPIService;

    /**
     * Constructs a new FeedServiceImpl with the specified FacebookGraphAPIService.
     * 
     * @param facebookGraphAPIService Service for interacting with the Facebook Graph API
     */
    public FeedServiceImpl(FacebookGraphAPIService facebookGraphAPIService) {
        this.facebookGraphAPIService = facebookGraphAPIService;
    }

    /**
     * {@inheritDoc}
     * 
     * <p>This implementation delegates to the FacebookGraphAPIService to fetch
     * all available feed posts.</p>
     * 
     * @return A list of all feed entities
     */
    @Override
    public List<FeedEntity> getAllFeeds() {
        log.debug("Récupération de tous les feeds Facebook");
        var feeds = this.facebookGraphAPIService.fetchFacebookFeeds();
        log.debug("{} feeds récupérés au total", feeds.size());
        return feeds;
    }

    /**
     * {@inheritDoc}
     * 
     * <p>This implementation fetches all feed posts from the FacebookGraphAPIService
     * and then applies pagination to the result using the toPage helper method.</p>
     * 
     * @param pageable Pagination information including page number, page size, and sorting
     * @return A page of feed entities
     */
    @Override
    public Page<FeedEntity> getFeeds(Pageable pageable) {
        log.debug("Récupération des feeds Facebook avec pagination: page={}, size={}", 
                pageable.getPageNumber(), pageable.getPageSize());
        var allFeeds = this.facebookGraphAPIService.fetchFacebookFeeds();
        var pagedFeeds = toPage(allFeeds, pageable);
        log.debug("{} feeds récupérés sur un total de {}", 
                pagedFeeds.getContent().size(), pagedFeeds.getTotalElements());
        return pagedFeeds;
    }


    /**
     * Converts a list to a paginated result based on the provided pagination parameters.
     * 
     * <p>This helper method takes a full list of items and extracts a subset based on
     * the page number and page size specified in the pageable parameter. It handles
     * edge cases such as when the requested page is beyond the available data.</p>
     * 
     * @param <T> The type of elements in the list
     * @param fullList The complete list of items to paginate
     * @param pageable Pagination information including page number and page size
     * @return A Page object containing the requested subset of items and pagination metadata
     */
    private <T> Page<T> toPage(List<T> fullList, Pageable pageable) {
        log.debug("Conversion d'une liste de {} éléments en page", fullList.size());
        int total = fullList.size();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), total);

        if (start > end) {
            log.debug("Index de début ({}) supérieur à l'index de fin ({}), retour d'une liste vide", start, end);
            return new PageImpl<>(List.of(), pageable, total);
        }

        List<T> content = fullList.subList(start, end);
        log.debug("Page créée avec {} éléments (de l'index {} à {})", content.size(), start, end);
        return new PageImpl<>(content, pageable, total);
    }
}
