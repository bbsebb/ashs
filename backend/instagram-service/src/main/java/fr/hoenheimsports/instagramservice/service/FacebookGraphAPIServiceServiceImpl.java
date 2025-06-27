package fr.hoenheimsports.instagramservice.service;

import fr.hoenheimsports.instagramservice.feignClient.FacebookGraphAPIFeignClient;
import fr.hoenheimsports.instagramservice.mapper.FeedEntityMapper;
import fr.hoenheimsports.instagramservice.model.FeedEntity;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class FacebookGraphAPIServiceServiceImpl implements FacebookGraphAPIService {
    private final static String ACCESS_TOKEN = "EAAT96BYHqBEBOxKaHZA11lN10IbFSxJFxcr5G2vLRzm2m5nxPzWp1ejQdQnLgxSc5BccSbsJ9oEbZAicWFgaZCMEZBRy3pZCErgEglyO7gydBPZBMlgnBZAoQiRSxqfUFP8qgCjSKpjqsNj3NMMFCdmZAgUJZBh8YigMQmZBTshFFRJpYcBgTVamVPNTHCveZAmeDZBbUjC5pMrcRVzbUvmitcohVqmR";
    private final FacebookGraphAPIFeignClient facebookGraphAPIFeignClient;
    private final FeedEntityMapper feedEntityMapper;

    public FacebookGraphAPIServiceServiceImpl(FacebookGraphAPIFeignClient facebookGraphAPIFeignClient, FeedEntityMapper feedEntityMapper) {
        this.facebookGraphAPIFeignClient = facebookGraphAPIFeignClient;
        this.feedEntityMapper = feedEntityMapper;
    }

    @Cacheable(value = "facebookFeeds")
    @Override
    public List<FeedEntity> saveFeeds() {
        var fields = URLEncoder.encode("id,created_time,message,attachments.limit(100){media_type,media,subattachments,type}", StandardCharsets.UTF_8);
        var apiGraph = this.facebookGraphAPIFeignClient.getFeed(fields, 100, ACCESS_TOKEN);
        if (apiGraph == null || apiGraph.data() == null || apiGraph.data().isEmpty()) {
            System.out.println("No data found");
            return List.of();
        }

        return apiGraph.data().stream().map(this.feedEntityMapper::toEntity).toList();

    }
}
