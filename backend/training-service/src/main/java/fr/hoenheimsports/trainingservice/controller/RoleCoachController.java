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

public interface RoleCoachController {

    @Operation(
            summary = "Get a Coach by ID",
            description = "This endpoint retrieves a Coach resource based on the provided ID."
    )
    @ApiResponse(responseCode = "200", description = "Coach successfully retrieved")
    @GetMapping("/{id}")
    ResponseEntity<EntityModel<RoleCoachDTOResponse>> getRoleCoachById(@PathVariable Long id);

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Remove a coach",
            description = "This endpoint remove a existing coach in team resource based on the provided IDs ."
    )
    @ApiResponse(responseCode = "200", description = "Coach successfully added")
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteRoleCoach(@PathVariable long id);
}
