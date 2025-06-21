package fr.hoenheimsports.instagramservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record ImageEmbeddable(
        int height,
        @Column(columnDefinition = "TEXT")
        String src,
        int width
) {
}
