package fr.hoenheimsports.facebookservice.controller.dto;

import fr.hoenheimsports.facebookservice.model.ImageEmbeddable;

import java.io.Serializable;

/**
 * DTO for {@link ImageEmbeddable}
 */
public record ImageDTOResponse(int height, String src, int width) implements Serializable {
}