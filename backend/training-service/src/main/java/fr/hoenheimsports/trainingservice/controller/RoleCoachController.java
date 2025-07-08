package fr.hoenheimsports.trainingservice.controller;

import fr.hoenheimsports.trainingservice.dto.response.RoleCoachDTOResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Controller interface defining the REST API endpoints for coach role operations.
 * 
 * <p>This interface provides endpoints for retrieving and deleting coach role relationships,
 * which represent the association between coaches and teams with specific roles (MAIN, ASSISTANT, etc.).
 * Some operations require administrative privileges.</p>
 * 
 * <p>All endpoints return HATEOAS-compliant responses with appropriate links to related resources.</p>
 * 
 * @since 1.0
 */
public interface RoleCoachController {

    /**
     * Retrieves a coach role relationship by its ID.
     * 
     * <p>This endpoint returns detailed information about a specific coach-team role relationship,
     * including the coach, team, and role type (MAIN, ASSISTANT, or SUPPORT_STAFF).</p>
     * 
     * @param id The unique identifier of the coach role relationship
     * @return A HATEOAS-compliant representation of the coach role relationship
     */
    @Operation(
            summary = "Get a coach role by ID",
            description = "This endpoint retrieves a coach role relationship based on the provided ID."
    )
    @ApiResponse(responseCode = "200", description = "Coach role successfully retrieved")
    @GetMapping("/{id}")
    ResponseEntity<EntityModel<RoleCoachDTOResponse>> getRoleCoachById(@PathVariable Long id);

    /**
     * Deletes a coach role relationship by its ID.
     * 
     * <p>This endpoint removes a specific coach-team role relationship from the system.
     * This operation requires administrative privileges and cannot be undone.</p>
     * 
     * @param id The unique identifier of the coach role relationship to delete
     * @return A ResponseEntity with HTTP status 204 (No Content) if the deletion was successful
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete a coach role",
            description = "This endpoint removes an existing coach role relationship based on the provided ID."
    )
    @ApiResponse(responseCode = "204", description = "Coach role successfully deleted")
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteRoleCoach(@PathVariable long id);
}
