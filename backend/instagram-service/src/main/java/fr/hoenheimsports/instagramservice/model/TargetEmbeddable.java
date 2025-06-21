package fr.hoenheimsports.instagramservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record TargetEmbeddable(
        String id,
        @Column(columnDefinition = "TEXT")
        String url
) {
}
