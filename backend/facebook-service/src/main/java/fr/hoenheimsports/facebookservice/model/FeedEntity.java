package fr.hoenheimsports.facebookservice.model;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a feed post from Facebook.
 * 
 * <p>This class stores information about a Facebook post including its unique identifier,
 * message content, creation time, and any attachments associated with the post.</p>
 * 
 * @since 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class FeedEntity {

    /**
     * The unique identifier of the post from the Facebook Graph API.
     */
    private String graphApiId;

    /**
     * The text content of the Facebook post.
     */
    private String message;

    /**
     * The date and time when the post was created on Facebook.
     */
    private OffsetDateTime createdTime;

    /**
     * List of attachments (images, videos, etc.) associated with this post.
     * This is a bidirectional relationship where the feed is the owner.
     */
    private List<AttachmentEntity> attachments = new ArrayList<>();

    /**
     * Adds an attachment to this feed and establishes the bidirectional relationship.
     * 
     * @param attachment The attachment to add to this feed, must not be null
     */
    public void addAttachment(AttachmentEntity attachment) {
        attachments.add(attachment);
        attachment.setFeed(this);
    }

    /**
     * Removes an attachment from this feed and breaks the bidirectional relationship.
     * 
     * @param attachment The attachment to remove from this feed, must not be null
     */
    public void removeAttachment(AttachmentEntity attachment) {
        attachments.remove(attachment);
        attachment.setFeed(null);
    }
}
