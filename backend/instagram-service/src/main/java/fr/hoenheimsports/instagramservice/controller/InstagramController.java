package fr.hoenheimsports.instagramservice.controller;

import fr.hoenheimsports.instagramservice.controller.dto.PostDTOResponse;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

public interface InstagramController {
    @GetMapping()
    ResponseEntity<CollectionModel<PostDTOResponse>> getMe();

    @GetMapping("/save")
    ResponseEntity<Void> save();


    @GetMapping("/all")
    List<PostDTOResponse> getAll();
}
