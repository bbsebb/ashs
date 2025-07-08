package fr.hoenheimsports.facebookservice.model;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing an attachment to a Facebook feed post.
 * 
 * <p>This class stores information about attachments such as images, videos, or other media
 * that are associated with a Facebook post. Each attachment can have multiple sub-attachments
 * and is linked to a specific feed post.</p>
 * 
 * @since 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class AttachmentEntity {

    /**
     * Unique identifier for the attachment.
     */
    private Long id;

    /**
     * The type of media in the attachment (e.g., "photo", "video").
     */
    private String mediaType;

    /**
     * The specific type of the attachment as defined by Facebook.
     */
    private String type;

    /**
     * Embedded media information for this attachment.
     */
    private MediaEmbeddable media;

    /**
     * The feed post this attachment belongs to.
     * This establishes a bidirectional relationship with FeedEntity.
     */
    private FeedEntity feed;

    /**
     * List of sub-attachments associated with this attachment.
     * This is a bidirectional relationship where the attachment is the owner.
     */
    private List<SubAttachmentEntity> subAttachments = new ArrayList<>();

    /**
     * Adds a sub-attachment to this attachment and establishes the bidirectional relationship.
     * 
     * @param subAttachment The sub-attachment to add to this attachment, must not be null
     */
    public void addSubAttachment(SubAttachmentEntity subAttachment) {
        subAttachments.add(subAttachment);
        subAttachment.setAttachment(this);
    }
}
