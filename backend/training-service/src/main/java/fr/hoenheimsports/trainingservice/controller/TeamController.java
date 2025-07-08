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

/**
 * Controller interface defining the REST API endpoints for team operations.
 * 
 * <p>This interface provides endpoints for creating, retrieving, updating, and deleting
 * team resources. It also includes endpoints for adding training sessions and coaches to teams.
 * It supports both individual team operations and operations on collections of teams,
 * including pagination. Some operations require administrative privileges.</p>
 * 
 * <p>All endpoints return HATEOAS-compliant responses with appropriate links to related resources.</p>
 * 
 * @since 1.0
 */
public interface TeamController {
    /**
     * Creates a new team resource based on the provided request body.
     * 
     * <p>This endpoint allows administrators to create a new team with details such as
     * gender, category, and team number. The team must have a unique combination of
     * these attributes.</p>
     * 
     * @param teamDTO The request containing the team details
     * @return A HATEOAS-compliant representation of the newly created team
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Create a new Team",
            description = "This endpoint creates a new Team resource based on the provided request body."
    )
    @ApiResponse(responseCode = "201", description = "Team successfully created")
    @PostMapping
    ResponseEntity<EntityModel<TeamDTOResponse>> createTeam(@Valid @RequestBody TeamDTOCreateRequest teamDTO);


    /**
     * Retrieves a team resource by its ID.
     * 
     * <p>This endpoint returns detailed information about a specific team,
     * including its gender, category, team number, associated coaches, and training sessions.</p>
     * 
     * @param id The unique identifier of the team to retrieve
     * @return A HATEOAS-compliant representation of the team
     */
    @Operation(
            summary = "Get a Team by ID",
            description = "This endpoint retrieves a Team resource based on the provided ID."
    )
    @ApiResponse(responseCode = "200", description = "Team successfully retrieved")
    @GetMapping("/{id}")
    ResponseEntity<EntityModel<TeamDTOResponse>> getTeamById(@PathVariable long id);

    /**
     * Retrieves a paginated list of team resources.
     * 
     * <p>This endpoint returns a page of teams based on the provided pagination parameters.
     * The response includes HATEOAS links for navigation between pages.</p>
     * 
     * @param pageable Pagination information including page number, page size, and sorting options
     * @return A HATEOAS-compliant paginated representation of teams
     */
    @Operation(
            summary = "Get all Teams",
            description = "This endpoint retrieves a paginated list of all Team resources."
    )
    @ApiResponse(responseCode = "200", description = "Team list successfully retrieved")
    @GetMapping
    ResponseEntity<PagedModel<EntityModel<TeamDTOResponse>>> getTeams(@ParameterObject Pageable pageable);


    /**
     * Retrieves a complete list of all team resources.
     * 
     * <p>This endpoint returns all available teams in the system without pagination.
     * The response includes HATEOAS links for each team.</p>
     * 
     * @return A HATEOAS-compliant collection of all teams
     */
    @Operation(
            summary = "Get all Teams",
            description = "This endpoint retrieves a list of all Team resources."
    )
    @ApiResponse(responseCode = "200", description = "Team list successfully retrieved")
    @GetMapping("/all")
    ResponseEntity<CollectionModel<EntityModel<TeamDTOResponse>>> getAllTeams();

    /**
     * Updates an existing team resource based on the provided ID and request body.
     * 
     * <p>This endpoint allows administrators to modify the details of an existing team,
     * such as its gender, category, or team number. The updated team must maintain a unique
     * combination of these attributes.</p>
     * 
     * @param id The unique identifier of the team to update
     * @param updatedTeamDTO The request containing the updated team details
     * @return A HATEOAS-compliant representation of the updated team
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update a Team",
            description = "This endpoint updates an existing Team resource based on the provided ID and request body."
    )
    @ApiResponse(responseCode = "200", description = "Team successfully updated")
    @PutMapping("/{id}")
    ResponseEntity<EntityModel<TeamDTOResponse>> updateTeam(@PathVariable long id, @Valid @RequestBody TeamDTOUpdateRequest updatedTeamDTO);

    /**
     * Deletes an existing team resource based on the provided ID.
     * 
     * <p>This endpoint allows administrators to permanently remove a team from the system.
     * If the team is associated with any coaches or training sessions, the operation may fail or
     * the associations may be deleted depending on the implementation.</p>
     * 
     * @param id The unique identifier of the team to delete
     * @return A ResponseEntity with HTTP status 204 (No Content) if the deletion was successful
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete a Team",
            description = "This endpoint deletes an existing Team resource based on the provided ID."
    )
    @ApiResponse(responseCode = "204", description = "Team successfully deleted")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    ResponseEntity<Void> deleteTeam(@PathVariable long id);

    /**
     * Adds a new training session to an existing team.
     * 
     * <p>This endpoint allows administrators to schedule a training session for a specific team.
     * The training session includes details such as day of week, start time, end time, and hall location.</p>
     * 
     * @param teamId The unique identifier of the team to add the training session to
     * @param addTrainingSessionInTeamDTORequest The request containing the training session details
     * @return A HATEOAS-compliant representation of the newly created training session
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Add a training session",
            description = "This endpoint add a new training session in team resource based on the provided ID and request body."
    )
    @ApiResponse(responseCode = "201", description = "Training session successfully added")
    @PostMapping("/{teamId}/training-sessions")
    ResponseEntity<EntityModel<TrainingSessionDTOResponse>> addTrainingSession(@PathVariable long teamId, @Valid @RequestBody AddTrainingSessionInTeamDTORequest addTrainingSessionInTeamDTORequest);


    /**
     * Adds a coach with a specific role to an existing team.
     * 
     * <p>This endpoint allows administrators to associate an existing coach with a team,
     * specifying the role the coach will have (MAIN, ASSISTANT, or SUPPORT_STAFF).
     * A coach can have different roles in different teams.</p>
     * 
     * @param teamId The unique identifier of the team to add the coach to
     * @param addCoachInTeamDTORequest The request containing the coach ID and role
     * @return A HATEOAS-compliant representation of the newly created coach-team relationship
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Add a coach",
            description = "This endpoint add a existing coach in team resource based on the provided IDs ."
    )
    @ApiResponse(responseCode = "201", description = "Coach successfully added")
    @PostMapping("/{teamId}/coach")
    ResponseEntity<EntityModel<RoleCoachDTOResponse>> addRoleCoach(@PathVariable long teamId, @Valid @RequestBody AddCoachInTeamDTORequest addCoachInTeamDTORequest);

}
