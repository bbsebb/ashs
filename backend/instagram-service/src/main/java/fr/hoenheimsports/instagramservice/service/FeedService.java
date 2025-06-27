package fr.hoenheimsports.instagramservice.service;

import fr.hoenheimsports.instagramservice.model.FeedEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FeedService {


    List<FeedEntity> getAllFeeds();

    Page<FeedEntity> getFeeds(Pageable pageable);

    FeedEntity getFeedById(String id);


}
