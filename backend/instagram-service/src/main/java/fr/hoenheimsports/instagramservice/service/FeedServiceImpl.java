package fr.hoenheimsports.instagramservice.service;

import fr.hoenheimsports.instagramservice.model.FeedEntity;
import fr.hoenheimsports.instagramservice.repository.FeedEntityRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedServiceImpl implements FeedService {
    private final FeedEntityRepository feedEntityRepository;
    private final FacebookGraphAPIService facebookGraphAPIService;

    public FeedServiceImpl(FeedEntityRepository feedEntityRepository, FacebookGraphAPIService facebookGraphAPIService) {
        this.feedEntityRepository = feedEntityRepository;
        this.facebookGraphAPIService = facebookGraphAPIService;
    }


    @Override
    public List<FeedEntity> getAllFeeds() {

        return this.facebookGraphAPIService.saveFeeds();
    }

    @Override
    public Page<FeedEntity> getFeeds(Pageable pageable) {

        return toPage(this.facebookGraphAPIService.saveFeeds(), pageable);
    }

    @Override
    public FeedEntity getFeedById(String id) {
        return this.feedEntityRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Feed introuvable avec l'id : " + id));
    }

    private static <T> Page<T> toPage(List<T> fullList, Pageable pageable) {
        int total = fullList.size();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), total);
        List<T> content = (start <= end) ? fullList.subList(start, end) : List.of();
        return new PageImpl<>(content, pageable, total);
    }
}
