package fr.hoenheimsports.trainingservice.controller;

import fr.hoenheimsports.trainingservice.dto.request.HallDTORequest;
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

public interface HallController {
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Create a new Hall",
            description = "This endpoint creates a new Hall resource based on the provided request body."
    )
    @ApiResponse(responseCode = "201", description = "Hall successfully created")
    @PostMapping
    ResponseEntity<EntityModel<HallDTOResponse>> createHall(@Valid @RequestBody HallDTORequest hallDTO);

    @Operation(
            summary = "Get a Hall by ID",
            description = "This endpoint retrieves a Hall resource based on the provided ID."
    )
    @ApiResponse(responseCode = "200", description = "Hall successfully retrieved")
    @GetMapping("/{id}")
    ResponseEntity<EntityModel<HallDTOResponse>> getHallById(@PathVariable Long id);


    @Operation(
            summary = "Get paginated Halls",
            description = "This endpoint retrieves a paginated list of all Hall resources."
    )
    @ApiResponse(responseCode = "200", description = "Paginated hall list successfully retrieved")
    @GetMapping
    ResponseEntity<PagedModel<EntityModel<HallDTOResponse>>> getHalls(@ParameterObject Pageable pageable);

    @Operation(
            summary = "Get all Halls",
            description = "This endpoint retrieves a list of all Hall resources."
    )
    @ApiResponse(responseCode = "200", description = "Hall list successfully retrieved")
    @GetMapping("/all")
    ResponseEntity<CollectionModel<EntityModel<HallDTOResponse>>> getAllHalls();

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update a Hall",
            description = "This endpoint updates an existing Hall resource based on the provided ID and request body."
    )
    @ApiResponse(responseCode = "200", description = "Hall successfully updated")
    @PutMapping("/{id}")
    ResponseEntity<EntityModel<HallDTOResponse>> updateHall(@PathVariable Long id, @Valid @RequestBody HallDTORequest updatedHallDTO);

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
