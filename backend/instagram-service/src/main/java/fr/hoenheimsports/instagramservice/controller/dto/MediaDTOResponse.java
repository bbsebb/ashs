package fr.hoenheimsports.instagramservice.controller.dto;

import fr.hoenheimsports.instagramservice.model.MediaEmbeddable;

import java.io.Serializable;

/**
 * DTO for {@link MediaEmbeddable}
 */
public record MediaDTOResponse(String source, ImageDTOResponse image) implements Serializable {
}