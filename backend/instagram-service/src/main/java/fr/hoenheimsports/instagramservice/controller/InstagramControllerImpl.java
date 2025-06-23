package fr.hoenheimsports.instagramservice.controller;

import fr.hoenheimsports.instagramservice.controller.dto.PostDTOResponse;
import fr.hoenheimsports.instagramservice.service.InstagramService;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api")
public class InstagramControllerImpl implements InstagramController {
    private final InstagramService instagramService;

    public InstagramControllerImpl(InstagramService instagramService) {
        this.instagramService = instagramService;
    }

    @GetMapping()
    @Override
    public ResponseEntity<CollectionModel<PostDTOResponse>> getMe() {
        Pageable pageable = Pageable.ofSize(1);
        var collection = CollectionModel.of(this.instagramService.getPaged(pageable));
        Link selfLink = linkTo(methodOn(InstagramControllerImpl.class).getMe()).withSelfRel();
        Link saveLink = linkTo(methodOn(InstagramControllerImpl.class).save()).withRel("save");
        collection.add(selfLink);
        collection.add(saveLink);
        return ResponseEntity.ok(collection);
    }

    @GetMapping("/save")
    @Override
    public ResponseEntity<Void> save() {
        this.instagramService.saveFeeds();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all")
    @Override
    public List<PostDTOResponse> getAll() {
        return this.instagramService.getAll();
    }
}
