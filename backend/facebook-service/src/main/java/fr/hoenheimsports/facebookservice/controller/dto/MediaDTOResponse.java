package fr.hoenheimsports.facebookservice.controller.dto;

import fr.hoenheimsports.facebookservice.model.MediaEmbeddable;

import java.io.Serializable;

/**
 * DTO for {@link MediaEmbeddable}
 */
public record MediaDTOResponse(String source, ImageDTOResponse image) implements Serializable {
}