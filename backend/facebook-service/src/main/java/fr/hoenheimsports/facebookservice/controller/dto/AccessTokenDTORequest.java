package fr.hoenheimsports.facebookservice.controller.dto;

import lombok.Builder;

@Builder
public record AccessTokenDTORequest(String accessToken) {
}
