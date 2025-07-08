package fr.hoenheimsports.trainingservice.controller;

import fr.hoenheimsports.trainingservice.dto.request.HallDTOCreateRequest;
import fr.hoenheimsports.trainingservice.dto.request.HallDTOUpdateRequest;
import fr.hoenheimsports.trainingservice.dto.response.HallDTOResponse;
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
 * Controller interface defining the REST API endpoints for hall operations.
 * 
 * <p>This interface provides endpoints for creating, retrieving, updating, and deleting
 * hall resources. Halls represent physical locations where training sessions take place.
 * It supports both individual hall operations and operations on collections of halls,
 * including pagination. Some operations require administrative privileges.</p>
 * 
 * <p>All endpoints return HATEOAS-compliant responses with appropriate links to related resources.</p>
 * 
 * @since 1.0
 */
public interface HallController {
    /**
     * Creates a new hall resource based on the provided request body.
     * 
     * <p>This endpoint allows administrators to create a new hall with details such as
     * name, address, and capacity. The hall must have a unique combination of name and address.</p>
     * 
     * @param hallDTO The request containing the hall details
     * @return A HATEOAS-compliant representation of the newly created hall
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Create a new Hall",
            description = "This endpoint creates a new Hall resource based on the provided request body."
    )
    @ApiResponse(responseCode = "201", description = "Hall successfully created")
    @PostMapping
    ResponseEntity<EntityModel<HallDTOResponse>> createHall(@Valid @RequestBody HallDTOCreateRequest hallDTO);

    /**
     * Retrieves a hall resource by its ID.
     * 
     * <p>This endpoint returns detailed information about a specific hall,
     * including its name, address, and capacity.</p>
     * 
     * @param id The unique identifier of the hall to retrieve
     * @return A HATEOAS-compliant representation of the hall
     */
    @Operation(
            summary = "Get a Hall by ID",
            description = "This endpoint retrieves a Hall resource based on the provided ID."
    )
    @ApiResponse(responseCode = "200", description = "Hall successfully retrieved")
    @GetMapping("/{id}")
    ResponseEntity<EntityModel<HallDTOResponse>> getHallById(@PathVariable Long id);


    /**
     * Retrieves a paginated list of hall resources.
     * 
     * <p>This endpoint returns a page of halls based on the provided pagination parameters.
     * The response includes HATEOAS links for navigation between pages.</p>
     * 
     * @param pageable Pagination information including page number, page size, and sorting options
     * @return A HATEOAS-compliant paginated representation of halls
     */
    @Operation(
            summary = "Get paginated Halls",
            description = "This endpoint retrieves a paginated list of all Hall resources."
    )
    @ApiResponse(responseCode = "200", description = "Paginated hall list successfully retrieved")
    @GetMapping
    ResponseEntity<PagedModel<EntityModel<HallDTOResponse>>> getHalls(@ParameterObject Pageable pageable);

    /**
     * Retrieves a complete list of all hall resources.
     * 
     * <p>This endpoint returns all available halls in the system without pagination.
     * The response includes HATEOAS links for each hall.</p>
     * 
     * @return A HATEOAS-compliant collection of all halls
     */
    @Operation(
            summary = "Get all Halls",
            description = "This endpoint retrieves a list of all Hall resources."
    )
    @ApiResponse(responseCode = "200", description = "Hall list successfully retrieved")
    @GetMapping("/all")
    ResponseEntity<CollectionModel<EntityModel<HallDTOResponse>>> getAllHalls();

    /**
     * Updates an existing hall resource based on the provided ID and request body.
     * 
     * <p>This endpoint allows administrators to modify the details of an existing hall,
     * such as its name, address, or capacity. The updated hall must maintain a unique
     * combination of name and address.</p>
     * 
     * @param id The unique identifier of the hall to update
     * @param updatedHallDTO The request containing the updated hall details
     * @return A HATEOAS-compliant representation of the updated hall
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update a Hall",
            description = "This endpoint updates an existing Hall resource based on the provided ID and request body."
    )
    @ApiResponse(responseCode = "200", description = "Hall successfully updated")
    @PutMapping("/{id}")
    ResponseEntity<EntityModel<HallDTOResponse>> updateHall(@PathVariable Long id, @Valid @RequestBody HallDTOUpdateRequest updatedHallDTO);

    /**
     * Deletes an existing hall resource based on the provided ID.
     * 
     * <p>This endpoint allows administrators to permanently remove a hall from the system.
     * If the hall is associated with any training sessions, the operation may fail or
     * the associations may be deleted depending on the implementation.</p>
     * 
     * @param id The unique identifier of the hall to delete
     * @return A ResponseEntity with HTTP status 204 (No Content) if the deletion was successful
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete a Hall",
            description = "This endpoint deletes an existing Hall resource based on the provided ID."
    )
    @ApiResponse(responseCode = "204", description = "Hall successfully deleted")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    ResponseEntity<Void> deleteHall(@PathVariable Long id);
}
