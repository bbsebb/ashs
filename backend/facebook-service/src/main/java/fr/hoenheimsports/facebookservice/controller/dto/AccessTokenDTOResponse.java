package fr.hoenheimsports.facebookservice.controller.dto;

import fr.hoenheimsports.facebookservice.model.AccessToken;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link AccessToken}
 */
public record AccessTokenDTOResponse(String tokenType, String accessToken, Instant expireIn) implements Serializable {
}