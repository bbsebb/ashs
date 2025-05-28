package fr.hoenheimsports.trainingservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

/**
 * The {@code ApiIndexModel} class represents the entry point of the API.
 * <p>
 * This model is utilized to provide links to all available resources in the API,
 * following HATEOAS principles.
 * </p>
 */
@Getter
@NoArgsConstructor
@Schema(description = "Represents the entry point of the API, providing links to all available resources.")
public class ApiIndexModel extends RepresentationModel<ApiIndexModel> {
    
    /**
     * Creates a new instance of {@link ApiIndexModel}.
     * 
     * @return A new instance of {@link ApiIndexModel}.
     */
    public static ApiIndexModel createApiIndex() {
        return new ApiIndexModel();
    }
}