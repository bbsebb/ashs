package fr.hoenheimsports.trainingservice.controller;

import fr.hoenheimsports.trainingservice.dto.request.TrainingSessionDTORequest;
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
 * Controller interface defining the REST API endpoints for training session operations.
 * 
 * <p>This interface provides endpoints for creating, retrieving, updating, and deleting
 * training session resources. Training sessions represent scheduled practice times for teams
 * at specific halls. The interface supports both individual training session operations and
 * operations on collections of training sessions, including pagination. Some operations
 * require administrative privileges.</p>
 * 
 * <p>All endpoints return HATEOAS-compliant responses with appropriate links to related resources.</p>
 * 
 * @since 1.0
 */
public interface TrainingSessionController {
    /**
     * Creates a new training session resource based on the provided request body.
     * 
     * <p>This endpoint allows administrators to create a new training session with details such as
     * day of week, start time, end time, and hall location. The training session must be associated
     * with a team.</p>
     * 
     * @param trainingSessionDTO The request containing the training session details
     * @return A HATEOAS-compliant representation of the newly created training session
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Create a new Training Session",
            description = "This endpoint creates a new TrainingSession resource based on the provided request body."
    )
    @ApiResponse(responseCode = "201", description = "TrainingSession successfully created")
    @PostMapping
    ResponseEntity<EntityModel<TrainingSessionDTOResponse>> createTrainingSession(
            @Valid @RequestBody TrainingSessionDTORequest trainingSessionDTO);

    /**
     * Retrieves a training session resource by its ID.
     * 
     * <p>This endpoint returns detailed information about a specific training session,
     * including its day of week, time slot, associated team, and hall location.</p>
     * 
     * @param id The unique identifier of the training session to retrieve
     * @return A HATEOAS-compliant representation of the training session
     */
    @Operation(
            summary = "Get a Training Session by ID",
            description = "This endpoint retrieves a specific TrainingSession resource based on its ID."
    )
    @ApiResponse(responseCode = "200", description = "TrainingSession successfully retrieved")
    @GetMapping("/{id}")
    ResponseEntity<EntityModel<TrainingSessionDTOResponse>> getTrainingSessionById(@PathVariable Long id);

    /**
     * Retrieves a paginated list of training session resources.
     * 
     * <p>This endpoint returns a page of training sessions based on the provided pagination parameters.
     * The response includes HATEOAS links for navigation between pages.</p>
     * 
     * @param pageable Pagination information including page number, page size, and sorting options
     * @return A HATEOAS-compliant paginated representation of training sessions
     */
    @Operation(
            summary = "Get all Training Sessions",
            description = "This endpoint retrieves a paginated list of all TrainingSessions."
    )
    @ApiResponse(responseCode = "200", description = "TrainingSessions successfully retrieved")
    @GetMapping
    ResponseEntity<PagedModel<EntityModel<TrainingSessionDTOResponse>>> getTrainingSessions(
            @ParameterObject Pageable pageable);

    /**
     * Updates an existing training session resource based on the provided ID and request body.
     * 
     * <p>This endpoint allows administrators to modify the details of an existing training session,
     * such as its day of week, start time, end time, or hall location. The training session must
     * remain associated with a team.</p>
     * 
     * @param id The unique identifier of the training session to update
     * @param trainingSessionDTO The request containing the updated training session details
     * @return A HATEOAS-compliant representation of the updated training session
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update a TrainingSession",
            description = "This endpoint updates an existing TrainingSession resource based on its ID."
    )
    @ApiResponse(responseCode = "200", description = "TrainingSession successfully updated")
    @PutMapping("/{id}")
    ResponseEntity<EntityModel<TrainingSessionDTOResponse>> updateTrainingSession(
            @PathVariable Long id,
            @Valid @RequestBody TrainingSessionDTORequest trainingSessionDTO);

    /**
     * Deletes an existing training session resource based on the provided ID.
     * 
     * <p>This endpoint allows administrators to permanently remove a training session from the system.
     * The training session will be disassociated from its team and hall.</p>
     * 
     * @param id The unique identifier of the training session to delete
     * @return A ResponseEntity with HTTP status 204 (No Content) if the deletion was successful
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete a TrainingSession",
            description = "This endpoint deletes an existing TrainingSession resource based on its ID."
    )
    @ApiResponse(responseCode = "204", description = "TrainingSession successfully deleted")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    ResponseEntity<Void> deleteTrainingSession(@PathVariable Long id);

    /**
     * Retrieves a complete list of all training session resources.
     * 
     * <p>This endpoint returns all available training sessions in the system without pagination.
     * The response includes HATEOAS links for each training session.</p>
     * 
     * @return A HATEOAS-compliant collection of all training sessions
     */
    @Operation(
            summary = "Get all Training Sessions",
            description = "This endpoint retrieves a list of all TrainingSessions."
    )
    @ApiResponse(responseCode = "200", description = "TrainingSessions successfully retrieved")
    @GetMapping("/all")
    ResponseEntity<CollectionModel<EntityModel<TrainingSessionDTOResponse>>> getAllTrainingSessions();
}
