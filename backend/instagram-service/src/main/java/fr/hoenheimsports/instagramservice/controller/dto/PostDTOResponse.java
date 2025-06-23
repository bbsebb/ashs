package fr.hoenheimsports.instagramservice.controller.dto;

import fr.hoenheimsports.instagramservice.model.PostEntity;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * DTO for {@link PostEntity}
 */
public record PostDTOResponse(String graphApiId, String message, OffsetDateTime createdTime,
                              List<AttachmentDTOResponse> attachments) implements Serializable {
}