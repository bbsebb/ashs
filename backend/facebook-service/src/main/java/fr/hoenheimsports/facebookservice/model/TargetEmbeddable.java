package fr.hoenheimsports.facebookservice.model;

/**
 * Represents target information for Facebook sub-attachments.
 * 
 * <p>This record encapsulates the target identifier and URL for sub-attachments,
 * typically used for links in Facebook posts. It is designed to be immutable and
 * embedded within SubAttachmentEntity objects.</p>
 * 
 * @since 1.0
 */
public record TargetEmbeddable(
        /**
         * The identifier of the target resource.
         */
        String id,

        /**
         * The URL of the target resource.
         */
        String url
) {
}
