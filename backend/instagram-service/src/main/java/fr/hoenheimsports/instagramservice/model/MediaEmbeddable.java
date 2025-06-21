package fr.hoenheimsports.instagramservice.model;

import jakarta.persistence.*;


@Embeddable
public record MediaEmbeddable(
        @Column(name = "media_source_url", columnDefinition = "TEXT")
        String source,

        @Embedded
        @AttributeOverrides({
                @AttributeOverride(name = "height", column = @Column(name = "media_image_height")),
                @AttributeOverride(name = "src", column = @Column(name = "media_image_src", columnDefinition = "TEXT")),
                @AttributeOverride(name = "width", column = @Column(name = "media_image_width"))
        })
        ImageEmbeddable image
) {
}
