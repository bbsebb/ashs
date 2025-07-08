package fr.hoenheimsports.facebookservice.feignClient.dto.error;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record FacebookGraphAPIErrorResponse(Error error) {
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Error(String message, String type, int code, int subcode, String fbtraceId) {
    }
}
