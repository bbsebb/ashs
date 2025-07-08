package fr.hoenheimsports.facebookservice.feignClient.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO for Facebook access token response.
 *
 * @param accessToken The access token string
 * @param tokenType   The type of token (usually "bearer")
 * @param expireIn    The token expiration time in seconds
 */
public record AccessTokenDTO(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("expires_in") int expireIn
) {
}