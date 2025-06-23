package fr.hoenheimsports.instagramservice.service;

import fr.hoenheimsports.instagramservice.controller.dto.PostDTOResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface InstagramService {
    String getMe();

    void saveFeeds();

    List<PostDTOResponse> getAll();

    Page<PostDTOResponse> getPaged(Pageable pageable);
}
