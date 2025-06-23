package fr.hoenheimsports.instagramservice.service;

import fr.hoenheimsports.instagramservice.controller.dto.PostDTOResponse;
import fr.hoenheimsports.instagramservice.feignClient.FacebookGraphAPI;
import fr.hoenheimsports.instagramservice.mapper.PostEntityMapper;
import fr.hoenheimsports.instagramservice.repository.PostEntityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class InstagramServiceImpl implements InstagramService {
    private final static String ACCESS_TOKEN = "EAAT96BYHqBEBOxKaHZA11lN10IbFSxJFxcr5G2vLRzm2m5nxPzWp1ejQdQnLgxSc5BccSbsJ9oEbZAicWFgaZCMEZBRy3pZCErgEglyO7gydBPZBMlgnBZAoQiRSxqfUFP8qgCjSKpjqsNj3NMMFCdmZAgUJZBh8YigMQmZBTshFFRJpYcBgTVamVPNTHCveZAmeDZBbUjC5pMrcRVzbUvmitcohVqmR";
    private final FacebookGraphAPI facebookGraphAPI;
    private final PostEntityMapper postEntityMapper;
    private final PostEntityRepository postEntityRepository;

    public InstagramServiceImpl(FacebookGraphAPI facebookGraphAPI, PostEntityMapper postEntityMapper, PostEntityRepository postEntityRepository) {
        this.facebookGraphAPI = facebookGraphAPI;
        this.postEntityMapper = postEntityMapper;
        this.postEntityRepository = postEntityRepository;
    }

    @Override
    public String getMe() {
        return this.facebookGraphAPI.getMe(ACCESS_TOKEN);
    }

    @Override
    @Transactional
    public void saveFeeds() {
        var fields = URLEncoder.encode("id,created_time,message,attachments.limit(100){media_type,media,subattachments,type}", StandardCharsets.UTF_8);
        var apiGraph = this.facebookGraphAPI.getFeed(fields, 100, ACCESS_TOKEN);
        if (apiGraph == null || apiGraph.data() == null || apiGraph.data().isEmpty()) {
            System.out.println("No data found");
            return;
        }
        var postEntity = this.postEntityMapper.toEntity(apiGraph.data().getFirst());
        apiGraph.data().stream().map(this.postEntityMapper::toEntity).forEach(this.postEntityRepository::save);
        this.postEntityRepository.save(postEntity);
    }

    @Override
    public List<PostDTOResponse> getAll() {
        return this.postEntityRepository.findAll().stream().map(this.postEntityMapper::toDto).toList();
    }

    @Override
    public Page<PostDTOResponse> getPaged(Pageable pageable) {
        return this.postEntityRepository.findAll(pageable).map(this.postEntityMapper::toDto);
    }
}
