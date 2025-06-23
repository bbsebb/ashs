package fr.hoenheimsports.instagramservice.controller.dto;

import fr.hoenheimsports.instagramservice.model.ImageEmbeddable;

import java.io.Serializable;

/**
 * DTO for {@link ImageEmbeddable}
 */
public record ImageDTOResponse(int height, String src, int width) implements Serializable {
}