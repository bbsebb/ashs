package fr.hoenheimsports.instagramservice.controller;

import fr.hoenheimsports.instagramservice.controller.dto.FeedDTOResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

public interface FeedController {
    @GetMapping()
    ResponseEntity<PagedModel<EntityModel<FeedDTOResponse>>> getFeeds(Pageable pageable);

    @GetMapping("/save")
    ResponseEntity<Void> save();


    @GetMapping("/all")
    ResponseEntity<CollectionModel<EntityModel<FeedDTOResponse>>> getAllFeeds();

    @GetMapping("/{id}")
    ResponseEntity<EntityModel<FeedDTOResponse>> getFeedById(@PathVariable String id);

    @PostMapping("/fetch-new")
    ResponseEntity<PagedModel<EntityModel<FeedDTOResponse>>> fetchNewFacebookPosts();
}
