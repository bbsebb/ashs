package fr.hoenheimsports.facebookservice.model;

import lombok.*;

/**
 * Entity representing a sub-attachment within a Facebook attachment.
 * 
 * <p>This class stores information about sub-attachments that are part of a larger attachment
 * in a Facebook post. Sub-attachments typically represent individual media items within
 * a gallery or carousel post.</p>
 * 
 * @since 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class SubAttachmentEntity {

    /**
     * Unique identifier for the sub-attachment.
     */
    private Long id;

    /**
     * The type of the sub-attachment as defined by Facebook.
     */
    private String type;

    /**
     * The URL to access the sub-attachment content.
     */
    private String url;

    /**
     * Embedded media information for this sub-attachment.
     */
    private MediaEmbeddable media;

    /**
     * Target information for this sub-attachment, typically used for links.
     */
    private TargetEmbeddable target;

    /**
     * The parent attachment this sub-attachment belongs to.
     * This establishes a bidirectional relationship with AttachmentEntity.
     */
    private AttachmentEntity attachment;
}
