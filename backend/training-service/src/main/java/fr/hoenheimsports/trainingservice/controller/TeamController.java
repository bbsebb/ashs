package fr.hoenheimsports.trainingservice.controller;

import fr.hoenheimsports.trainingservice.dto.request.AddCoachInTeamDTORequest;
import fr.hoenheimsports.trainingservice.dto.request.AddTrainingSessionInTeamDTORequest;
import fr.hoenheimsports.trainingservice.dto.request.TeamDTOCreateRequest;
import fr.hoenheimsports.trainingservice.dto.request.TeamDTOUpdateRequest;
import fr.hoenheimsports.trainingservice.dto.response.RoleCoachDTOResponse;
import fr.hoenheimsports.trainingservice.dto.response.TeamDTOResponse;
import fr.hoenheimsports.trainingservice.dto.response.TrainingSessionDTOResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

public interface TeamController {
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Create a new Team",
            description = "This endpoint creates a new Team resource based on the provided request body."
    )
    @ApiResponse(responseCode = "201", description = "Team successfully created")
    @PostMapping
    ResponseEntity<EntityModel<TeamDTOResponse>> createTeam(@Valid @RequestBody TeamDTOCreateRequest teamDTO);


    @Operation(
            summary = "Get a Team by ID",
            description = "This endpoint retrieves a Team resource based on the provided ID."
    )
    @ApiResponse(responseCode = "200", description = "Team successfully retrieved")
    @GetMapping("/{id}")
    ResponseEntity<EntityModel<TeamDTOResponse>> getTeamById(@PathVariable long id);

    @Operation(
            summary = "Get all Teams",
            description = "This endpoint retrieves a paginated list of all Team resources."
    )
    @ApiResponse(responseCode = "200", description = "Team list successfully retrieved")
    @GetMapping
    ResponseEntity<PagedModel<EntityModel<TeamDTOResponse>>> getTeams(@ParameterObject Pageable pageable);


    @Operation(
            summary = "Get all Teams",
            description = "This endpoint retrieves a list of all Team resources."
    )
    @ApiResponse(responseCode = "200", description = "Team list successfully retrieved")
    @GetMapping("/all")
    ResponseEntity<CollectionModel<EntityModel<TeamDTOResponse>>> getAllTeams();

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update a Team",
            description = "This endpoint updates an existing Team resource based on the provided ID and request body."
    )
    @ApiResponse(responseCode = "200", description = "Team successfully updated")
    @PutMapping("/{id}")
    ResponseEntity<EntityModel<TeamDTOResponse>> updateTeam(@PathVariable long id, @Valid @RequestBody TeamDTOUpdateRequest updatedTeamDTO);

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete a Team",
            description = "This endpoint deletes an existing Team resource based on the provided ID."
    )
    @ApiResponse(responseCode = "204", description = "Team successfully deleted")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    ResponseEntity<Void> deleteTeam(@PathVariable long id);

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Add a training session",
            description = "This endpoint add a new training session in team resource based on the provided ID and request body."
    )
    @ApiResponse(responseCode = "201", description = "Training session successfully added")
    @PostMapping("/{teamId}/training-sessions")
    ResponseEntity<EntityModel<TrainingSessionDTOResponse>> addTrainingSession(@PathVariable long teamId, @Valid @RequestBody AddTrainingSessionInTeamDTORequest addTrainingSessionInTeamDTORequest);


    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Add a coach",
            description = "This endpoint add a existing coach in team resource based on the provided IDs ."
    )
    @ApiResponse(responseCode = "201", description = "Coach successfully added")
    @PostMapping("/{teamId}/coach")
    ResponseEntity<EntityModel<RoleCoachDTOResponse>> addRoleCoach(@PathVariable long teamId, @Valid @RequestBody AddCoachInTeamDTORequest addCoachInTeamDTORequest);

}
