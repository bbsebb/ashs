package fr.hoenheimsports.facebookservice.controller;

import fr.hoenheimsports.facebookservice.assembler.FeedAssembler;
import fr.hoenheimsports.facebookservice.controller.dto.FeedDTOResponse;
import fr.hoenheimsports.facebookservice.service.FeedService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Implementation of the FeedController interface for handling Facebook feed operations.
 * 
 * <p>This controller provides REST API endpoints for retrieving Facebook feed posts
 * from the organization's Facebook page. It delegates the actual data retrieval to
 * the FeedService and uses a FeedAssembler to convert the domain entities to
 * HATEOAS-enabled DTOs with appropriate links.</p>
 * 
 * @since 1.0
 */
@RestController
@RequestMapping("/api/feeds")
@Slf4j
public class FeedControllerImpl implements FeedController {
    /**
     * The feed service used to retrieve Facebook feed posts.
     */
    private final FeedService feedService;

    /**
     * The assembler used to convert feed entities to DTOs with HATEOAS links.
     */
    private final FeedAssembler feedAssembler;

    /**
     * Constructs a new FeedControllerImpl with the specified dependencies.
     * 
     * @param feedService The service to use for retrieving Facebook feed posts
     * @param feedAssembler The assembler to use for converting feed entities to DTOs with HATEOAS links
     */
    public FeedControllerImpl(FeedService feedService, FeedAssembler feedAssembler) {
        this.feedService = feedService;
        this.feedAssembler = feedAssembler;
    }

    /**
     * {@inheritDoc}
     * 
     * <p>This implementation delegates to the FeedService to retrieve a paginated list of feeds,
     * then uses the FeedAssembler to convert them to a PagedModel with HATEOAS links.
     * It logs the request parameters and response size for monitoring purposes.</p>
     * 
     * @param pageable Pagination information including page number, page size, and sorting
     * @return A ResponseEntity containing a paged model of feed posts with HATEOAS links
     */
    @GetMapping()
    @Override
    public ResponseEntity<PagedModel<EntityModel<FeedDTOResponse>>> getFeeds(Pageable pageable) {
        log.info("Réception d'une requête pour obtenir les feeds paginés");
        log.debug("Paramètres de pagination: page={}, size={}, sort={}", 
                pageable.getPageNumber(), pageable.getPageSize(), 
                pageable.getSort().toString());
        var result = this.feedAssembler.toPagedModel(this.feedService.getFeeds(pageable));
        log.info("Retour de {} feeds paginés", result.getMetadata().getTotalElements());
        return ResponseEntity.ok(result);
    }

    /**
     * {@inheritDoc}
     * 
     * <p>This implementation delegates to the FeedService to retrieve all feeds,
     * then uses the FeedAssembler to convert them to a CollectionModel with HATEOAS links.
     * It logs the request and response size for monitoring purposes.</p>
     * 
     * @return A ResponseEntity containing a collection model of all feed posts with HATEOAS links
     */
    @GetMapping("/all")
    @Override
    public ResponseEntity<CollectionModel<EntityModel<FeedDTOResponse>>> getAllFeeds() {
        log.info("Réception d'une requête pour obtenir tous les feeds");
        var feeds = this.feedService.getAllFeeds();
        var result = this.feedAssembler.toCollectionModel(feeds);
        log.info("Retour de {} feeds au total", feeds.size());
        return ResponseEntity.ok(result);
    }
}
