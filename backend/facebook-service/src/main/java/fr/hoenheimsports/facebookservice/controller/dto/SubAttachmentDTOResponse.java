package fr.hoenheimsports.facebookservice.controller.dto;

import fr.hoenheimsports.facebookservice.model.SubAttachmentEntity;

import java.io.Serializable;

/**
 * DTO for {@link SubAttachmentEntity}
 */
public record SubAttachmentDTOResponse(Long id, String type, String url, MediaDTOResponse media,
                                       TargetDTOResponse target) implements Serializable {
}