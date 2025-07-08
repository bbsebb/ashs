package fr.hoenheimsports.facebookservice.controller.dto;

import fr.hoenheimsports.facebookservice.model.AttachmentEntity;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link AttachmentEntity}
 */
public record AttachmentDTOResponse(Long id, String mediaType, String type, MediaDTOResponse media,
                                    List<SubAttachmentDTOResponse> subAttachments) implements Serializable {
}