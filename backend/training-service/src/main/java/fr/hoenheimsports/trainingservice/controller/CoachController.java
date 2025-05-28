package fr.hoenheimsports.trainingservice.controller;

import fr.hoenheimsports.trainingservice.dto.request.CoachDTORequest;
import fr.hoenheimsports.trainingservice.dto.response.CoachDTOResponse;
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

public interface CoachController {

    /**
     * Creates a new Coach resource based on the provided request body.
     *
     * @param coachDTO the CoachDTORequest containing data for creating a new Coach
     * @return a HATEOAS-compliant representation of the newly created Coach
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Create a new Coach",
            description = "This endpoint creates a new Coach resource based on the provided request body."
    )
    @ApiResponse(responseCode = "201", description = "Coach successfully created")
    @PostMapping
    ResponseEntity<EntityModel<CoachDTOResponse>> createCoach(@Valid @RequestBody CoachDTORequest coachDTO);


    /**
     * Retrieves a Coach resource based on the provided ID.
     *
     * @param id the unique identifier of the Coach
     * @return a HATEOAS-compliant representation of the retrieved Coach
     */
    @Operation(
            summary = "Get a Coach by ID",
            description = "This endpoint retrieves a Coach resource based on the provided ID."
    )
    @ApiResponse(responseCode = "200", description = "Coach successfully retrieved")
    @GetMapping("/{id}")
    ResponseEntity<EntityModel<CoachDTOResponse>> getCoachById(@PathVariable Long id);


    /**
     * Retrieves a paginated list of all Coach resources.
     *
     * @param pageable pagination information, including page number, size, and sorting options
     * @return a HATEOAS-compliant paginated representation of all Coaches
     */
    @Operation(
            summary = "Get paginated Coaches",
            description = "This endpoint retrieves a paginated list of Coach resources."
    )
    @ApiResponse(responseCode = "200", description = "Coach list successfully retrieved")
    @GetMapping
    ResponseEntity<PagedModel<EntityModel<CoachDTOResponse>>> getCoaches(@ParameterObject Pageable pageable);


    /**
     * Retrieves a list of all Coach resources.
     *
     * @return a HATEOAS-compliant representation of all Coaches
     */
    @Operation(
            summary = "Get all Coaches",
            description = "This endpoint retrieves a list of all Coach resources."
    )
    @ApiResponse(responseCode = "200", description = "Coach list successfully retrieved")
    @GetMapping("/all")
    ResponseEntity<CollectionModel<EntityModel<CoachDTOResponse>>> getAllCoaches();


    /**
     * Updates an existing Coach resource based on the provided ID and request body.
     *
     * @param id              the unique identifier of the Coach to update
     * @param updatedCoachDTO the CoachDTORequest containing the updated data
     * @return a HATEOAS-compliant representation of the updated Coach
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update a Coach",
            description = "This endpoint updates an existing Coach resource based on the provided ID and request body."
    )
    @ApiResponse(responseCode = "200", description = "Coach successfully updated")
    @PutMapping("/{id}")
    ResponseEntity<EntityModel<CoachDTOResponse>> updateCoach(@PathVariable Long id, @Valid @RequestBody CoachDTORequest updatedCoachDTO);


    /**
     * Deletes an existing Coach resource based on the provided ID.
     *
     * @param id the unique identifier of the Coach to delete
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete a Coach",
            description = "This endpoint deletes an existing Coach resource based on the provided ID."
    )
    @ApiResponse(responseCode = "204", description = "Coach successfully deleted")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    ResponseEntity<Void> deleteCoach(@PathVariable Long id);
}
