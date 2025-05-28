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

public interface TrainingSessionController {
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Create a new Training Session",
            description = "This endpoint creates a new TrainingSession resource based on the provided request body."
    )
    @ApiResponse(responseCode = "201", description = "TrainingSession successfully created")
    @PostMapping
    ResponseEntity<EntityModel<TrainingSessionDTOResponse>> createTrainingSession(
            @Valid @RequestBody TrainingSessionDTORequest trainingSessionDTO);

    @Operation(
            summary = "Get a Training Session by ID",
            description = "This endpoint retrieves a specific TrainingSession resource based on its ID."
    )
    @ApiResponse(responseCode = "200", description = "TrainingSession successfully retrieved")
    @GetMapping("/{id}")
    ResponseEntity<EntityModel<TrainingSessionDTOResponse>> getTrainingSessionById(@PathVariable Long id);

    @Operation(
            summary = "Get all Training Sessions",
            description = "This endpoint retrieves a paginated list of all TrainingSessions."
    )
    @ApiResponse(responseCode = "200", description = "TrainingSessions successfully retrieved")
    @GetMapping
    ResponseEntity<PagedModel<EntityModel<TrainingSessionDTOResponse>>> getTrainingSessions(
            @ParameterObject Pageable pageable);

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

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete a TrainingSession",
            description = "This endpoint deletes an existing TrainingSession resource based on its ID."
    )
    @ApiResponse(responseCode = "204", description = "TrainingSession successfully deleted")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    ResponseEntity<Void> deleteTrainingSession(@PathVariable Long id);

    @Operation(
            summary = "Get all Training Sessions",
            description = "This endpoint retrieves a list of all TrainingSessions."
    )
    @ApiResponse(responseCode = "200", description = "TrainingSessions successfully retrieved")
    @GetMapping("/all")
    ResponseEntity<CollectionModel<EntityModel<TrainingSessionDTOResponse>>> getAllTrainingSessions();
}
