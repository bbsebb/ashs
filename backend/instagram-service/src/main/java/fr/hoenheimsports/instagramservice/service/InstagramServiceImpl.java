package fr.hoenheimsports.instagramservice.service;

import fr.hoenheimsports.instagramservice.feignClient.FacebookGraphAPI;
import org.springframework.stereotype.Service;

@Service
public class InstagramServiceImpl implements InstagramService {
    private final static String ACCESS_TOKEN = "EAAT96BYHqBEBOxKaHZA11lN10IbFSxJFxcr5G2vLRzm2m5nxPzWp1ejQdQnLgxSc5BccSbsJ9oEbZAicWFgaZCMEZBRy3pZCErgEglyO7gydBPZBMlgnBZAoQiRSxqfUFP8qgCjSKpjqsNj3NMMFCdmZAgUJZBh8YigMQmZBTshFFRJpYcBgTVamVPNTHCveZAmeDZBbUjC5pMrcRVzbUvmitcohVqmR";
    private final FacebookGraphAPI facebookGraphAPI;

    public InstagramServiceImpl(FacebookGraphAPI facebookGraphAPI) {
        this.facebookGraphAPI = facebookGraphAPI;
    }

    @Override
    public String getMe() {
        return this.facebookGraphAPI.getMe(ACCESS_TOKEN);
    }
}
