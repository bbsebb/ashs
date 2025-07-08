package fr.hoenheimsports.facebookservice.controller.dto;

import fr.hoenheimsports.facebookservice.model.TargetEmbeddable;

import java.io.Serializable;

/**
 * DTO for {@link TargetEmbeddable}
 */
public record TargetDTOResponse(String id, String url) implements Serializable {
}