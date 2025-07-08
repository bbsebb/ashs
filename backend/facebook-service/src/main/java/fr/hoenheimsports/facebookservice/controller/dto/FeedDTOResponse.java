package fr.hoenheimsports.facebookservice.controller.dto;

import fr.hoenheimsports.facebookservice.model.FeedEntity;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * DTO for {@link FeedEntity}
 */
public record FeedDTOResponse(String graphApiId, String message, OffsetDateTime createdTime,
                              List<AttachmentDTOResponse> attachments) implements Serializable {
}