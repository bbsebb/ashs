package fr.hoenheimsports.facebookservice.service;

import fr.hoenheimsports.facebookservice.model.FeedEntity;

import java.util.List;

/**
 * Service interface for interacting with the Facebook Graph API.
 * 
 * <p>This interface defines the contract for fetching data from the Facebook Graph API,
 * specifically feed posts from the organization's Facebook page.</p>
 * 
 * @since 1.0
 */
public interface FacebookGraphAPIService {

    /**
     * Fetches the latest feed posts from the organization's Facebook page.
     * 
     * <p>This method retrieves feed posts from the Facebook Graph API, including
     * any attachments such as images or videos associated with the posts.</p>
     * 
     * @return A list of feed entities containing the posts and their attachments
     */
    List<FeedEntity> fetchFacebookFeeds();
}
