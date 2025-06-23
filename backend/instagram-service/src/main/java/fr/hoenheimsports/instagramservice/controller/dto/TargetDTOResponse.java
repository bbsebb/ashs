package fr.hoenheimsports.instagramservice.controller.dto;

import fr.hoenheimsports.instagramservice.model.TargetEmbeddable;

import java.io.Serializable;

/**
 * DTO for {@link TargetEmbeddable}
 */
public record TargetDTOResponse(String id, String url) implements Serializable {
}