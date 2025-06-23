package fr.hoenheimsports.instagramservice.controller.dto;

import fr.hoenheimsports.instagramservice.model.SubAttachmentEntity;

import java.io.Serializable;

/**
 * DTO for {@link SubAttachmentEntity}
 */
public record SubAttachmentDTOResponse(Long id, String type, String url, MediaDTOResponse media,
                                       TargetDTOResponse target) implements Serializable {
}