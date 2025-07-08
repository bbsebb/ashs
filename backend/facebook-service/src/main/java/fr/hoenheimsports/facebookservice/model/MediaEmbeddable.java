package fr.hoenheimsports.facebookservice.model;

/**
 * Represents media information for Facebook attachments.
 * 
 * <p>This record encapsulates the source URL and image information for media content
 * in Facebook attachments and sub-attachments. It is designed to be immutable and
 * embedded within other entities.</p>
 * 
 * @since 1.0
 */
public record MediaEmbeddable(
        /**
         * The source URL of the media content.
         */
        String source,

        /**
         * Image information associated with this media.
         */
        ImageEmbeddable image
) {
}
