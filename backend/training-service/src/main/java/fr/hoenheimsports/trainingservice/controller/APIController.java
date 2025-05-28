package fr.hoenheimsports.trainingservice.controller;

import fr.hoenheimsports.trainingservice.dto.ApiIndexModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * <p>REST controller for providing the entry point of the API.</p>
 *
 * <p>This controller provides:</p>
 * <ul>
 *   <li>A single entry point for the API</li>
 *   <li>HATEOAS-compliant representation with links to all available resources</li>
 * </ul>
 */
@RequestMapping("/api")
public interface APIController {

    /**
     * Get the entry point of the API.
     *
     * @return A HATEOAS-compliant representation with links to all available resources
     */
    @Operation(
            summary = "Get API entry point",
            description = "This endpoint provides the entry point of the API with links to all available resources."
    )
    @ApiResponse(responseCode = "200", description = "API entry point successfully retrieved")
    @GetMapping
    ResponseEntity<ApiIndexModel> getApiIndex();
}
