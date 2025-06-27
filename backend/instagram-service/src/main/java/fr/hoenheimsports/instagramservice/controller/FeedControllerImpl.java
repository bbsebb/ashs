package fr.hoenheimsports.instagramservice.controller;

import fr.hoenheimsports.instagramservice.assembler.FeedAssembler;
import fr.hoenheimsports.instagramservice.controller.dto.FeedDTOResponse;
import fr.hoenheimsports.instagramservice.service.FacebookGraphAPIService;
import fr.hoenheimsports.instagramservice.service.FeedService;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feeds")
public class FeedControllerImpl implements FeedController {
    private final FeedService feedService;
    private final FacebookGraphAPIService facebookGraphAPIService;
    private final FeedAssembler feedAssembler;

    public FeedControllerImpl(FeedService feedService, FacebookGraphAPIService facebookGraphAPIService, FeedAssembler feedAssembler) {
        this.feedService = feedService;
        this.facebookGraphAPIService = facebookGraphAPIService;
        this.feedAssembler = feedAssembler;
    }

    @GetMapping()
    @Override
    public ResponseEntity<PagedModel<EntityModel<FeedDTOResponse>>> getFeeds(Pageable pageable) {

        return ResponseEntity.ok(this.feedAssembler.toPagedModel(this.feedService.getFeeds(pageable)));
    }

    @GetMapping("/save")
    @Override
    public ResponseEntity<Void> save() {
        this.facebookGraphAPIService.saveFeeds();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all")
    @Override
    public ResponseEntity<CollectionModel<EntityModel<FeedDTOResponse>>> getAllFeeds() {
        return ResponseEntity.ok(this.feedAssembler.toCollectionModel(this.feedService.getAllFeeds()));
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<EntityModel<FeedDTOResponse>> getFeedById(@PathVariable String id) {
        return ResponseEntity.ok(this.feedAssembler.toModel(this.feedService.getFeedById(id)));
    }

    @PostMapping("/fetch-new")
    @Override
    public ResponseEntity<PagedModel<EntityModel<FeedDTOResponse>>> fetchNewFacebookPosts() {
        return this.getFeeds(Pageable.unpaged());
    }


}
