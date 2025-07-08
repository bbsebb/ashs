package fr.hoenheimsports.facebookservice.feignClient.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Représente une publication individuelle dans le flux.
 *
 * @param id          L'identifiant unique de la publication.
 * @param createdTime La date et l'heure de création de la publication.
 * @param message     Le message texte de la publication (peut être nul).
 * @param attachments Les pièces jointes associées à la publication.
 */
public record FeedDTO(
        String id,
        @JsonProperty("created_time") String createdTime,
        String message,
        AttachmentsDTO attachments
) {
}