package fr.hoenheimsports.facebookservice.service;

import fr.hoenheimsports.facebookservice.model.FeedEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for retrieving Facebook feed posts.
 * 
 * <p>This interface defines the contract for accessing Facebook feed posts
 * that have been fetched from the Facebook Graph API. It provides methods
 * for retrieving all feeds or a paginated subset of feeds.</p>
 * 
 * @since 1.0
 */
public interface FeedService {

    /**
     * Retrieves all Facebook feed posts.
     * 
     * <p>This method returns all available feed posts from the organization's
     * Facebook page, including any attachments such as images or videos.</p>
     * 
     * @return A list of all feed entities
     */
    List<FeedEntity> getAllFeeds();

    /**
     * Retrieves a paginated subset of Facebook feed posts.
     * 
     * <p>This method returns a page of feed posts from the organization's
     * Facebook page based on the provided pagination parameters.</p>
     * 
     * @param pageable Pagination information including page number, page size, and sorting
     * @return A page of feed entities
     */
    Page<FeedEntity> getFeeds(Pageable pageable);

}
