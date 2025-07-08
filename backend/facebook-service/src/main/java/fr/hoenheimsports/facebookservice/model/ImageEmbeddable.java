package fr.hoenheimsports.facebookservice.model;

/**
 * Represents image information for Facebook media content.
 * 
 * <p>This record encapsulates the dimensions and source URL of an image
 * associated with Facebook media content. It is designed to be immutable and
 * embedded within MediaEmbeddable entities.</p>
 * 
 * @since 1.0
 */
public record ImageEmbeddable(
        /**
         * The height of the image in pixels.
         */
        int height,

        /**
         * The source URL of the image.
         */
        String src,

        /**
         * The width of the image in pixels.
         */
        int width
) {
}
