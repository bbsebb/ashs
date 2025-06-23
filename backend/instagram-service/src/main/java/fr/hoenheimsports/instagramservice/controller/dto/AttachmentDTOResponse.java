package fr.hoenheimsports.instagramservice.controller.dto;

import fr.hoenheimsports.instagramservice.model.AttachmentEntity;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link AttachmentEntity}
 */
public record AttachmentDTOResponse(Long id, String mediaType, String type, MediaDTOResponse media,
                                    List<SubAttachmentDTOResponse> subAttachments) implements Serializable {
}