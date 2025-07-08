package fr.hoenheimsports.facebookservice.controller;

import fr.hoenheimsports.facebookservice.controller.dto.FeedDTOResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller interface defining the REST API endpoints for Facebook feed operations.
 * 
 * <p>This interface provides endpoints for retrieving Facebook feed posts
 * from the organization's Facebook page, either as a paginated subset or as a complete list.</p>
 * 
 * @since 1.0
 */
public interface FeedController {
    /**
     * Retrieves a paginated subset of Facebook feed posts.
     * 
     * <p>This endpoint returns a page of feed posts from the organization's
     * Facebook page based on the provided pagination parameters. The response
     * includes HATEOAS links for navigation.</p>
     * 
     * @param pageable Pagination information including page number, page size, and sorting
     * @return A ResponseEntity containing a paged model of feed posts with HATEOAS links
     */
    @GetMapping()
    ResponseEntity<PagedModel<EntityModel<FeedDTOResponse>>> getFeeds(Pageable pageable);

    /**
     * Retrieves all Facebook feed posts.
     * 
     * <p>This endpoint returns all available feed posts from the organization's
     * Facebook page. The response includes HATEOAS links.</p>
     * 
     * @return A ResponseEntity containing a collection model of all feed posts with HATEOAS links
     */
    @GetMapping("/all")
    ResponseEntity<CollectionModel<EntityModel<FeedDTOResponse>>> getAllFeeds();
}
