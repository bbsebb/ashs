package fr.hoenheimsports.contactservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

@Getter
@Setter
@AllArgsConstructor
@Builder
@Schema(description = "Represents an error response returned by the API in case of failures.")
public class ApiErrorModel extends RepresentationModel<ApiErrorModel> {

    @Schema(description = "Type of the error, usually a URI reference identifying the problem type.", example = "https://example.com/probs/email-error")
    private String type;

    @Schema(description = "A short, human-readable summary of the problem type.", example = "Invalid email address")
    private String title;

    @Schema(description = "HTTP status code for this problem.", example = "400")
    private int status;

    @Schema(description = "A human-readable explanation specific to this occurrence of the problem.", example = "The provided email address is not in a valid format.")
    private String detail;

    @Schema(description = "A URI reference that identifies the specific occurrence of the problem.", example = "/sendEmail/12345")
    private String instance;

    public static ApiErrorModel createError(String type, String title, int status, String detail, String instance) {
        // Adding HATEOAS links (if needed)
        return new ApiErrorModel(type, title, status, detail, instance);
    }
}

