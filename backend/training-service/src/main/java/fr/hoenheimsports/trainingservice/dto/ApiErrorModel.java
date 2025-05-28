package fr.hoenheimsports.trainingservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

/**
 * The {@code ApiErrorModel} class represents a standard structure for error responses returned by the API.
 * <p>
 * This model is utilized to provide detailed information about problems that have occurred during API calls,
 * such as validation failures or server-side exceptions.
 * </p>
 * <p>
 * It includes fields to describe the type, title, status code, details, and a specific instance
 * related to the error. It is also compatible with HATEOAS for providing resource links, if needed.
 * </p>
 *
 * <b>Example usage:</b>
 * <pre>
 * {@code
 * ApiErrorModel error = ApiErrorModel.createError(
 *     "https://example.com/problem/email-error",
 *     "Invalid email address",
 *     400,
 *     "The provided email address is not in a valid format.",
 *     "/sendEmail/12345"
 * );
 * }
 * </pre>
 */
@Getter
@Setter
@AllArgsConstructor
@Builder
@Schema(description = "Represents an error response returned by the API in case of failures.")
public class ApiErrorModel extends RepresentationModel<ApiErrorModel> {

    @Schema(description = "Type of the error, typically a URI or string identifying the problem type.", example = "https://example.com/problems/error-type")
    private String type;

    @Schema(description = "A brief, descriptive summary of the problem.", example = "Invalid input format")
    private String title;

    @Schema(description = "HTTP status code representing this error.", example = "500")
    private int status;

    @Schema(description = "A detailed description of this specific error occurrence.", example = "A required field is missing or invalid.")
    private String detail;

    @Schema(description = "A URI or string that identifies the specific occurrence of the problem.", example = "/api/resource/123")
    private String instance;

    /**
     * Creates an instance of {@link ApiErrorModel} to represent a specific type of error.
     *
     * @param type     The type of the error, usually a URI reference.
     * @param title    A short, human-readable summary of the problem.
     * @param status   HTTP status code associated with the error.
     * @param detail   A detailed explanation specific to this error occurrence.
     * @param instance A URI reference identifying the specific instance of the problem.
     * @return An instance of {@link ApiErrorModel}.
     */
    public static ApiErrorModel createError(String type, String title, int status, String detail, String instance) {
        // Adding HATEOAS links (if needed)
        return new ApiErrorModel(type, title, status, detail, instance);
    }
}

