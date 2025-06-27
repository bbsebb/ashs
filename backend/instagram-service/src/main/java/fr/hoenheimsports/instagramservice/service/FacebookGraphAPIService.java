package fr.hoenheimsports.instagramservice.service;

import fr.hoenheimsports.instagramservice.model.FeedEntity;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

public interface FacebookGraphAPIService {
    @Cacheable(value = "facebookFeeds")
    List<FeedEntity> saveFeeds();
}
