package fr.hoenheimsports.trainingservice.controller;

import fr.hoenheimsports.trainingservice.assembler.CoachAssembler;
import fr.hoenheimsports.trainingservice.dto.request.CoachDTORequest;
import fr.hoenheimsports.trainingservice.dto.response.CoachDTOResponse;
import fr.hoenheimsports.trainingservice.mapper.CoachMapper;
import fr.hoenheimsports.trainingservice.model.Coach;
import fr.hoenheimsports.trainingservice.service.CoachService;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>REST controller for managing Coach-related operations.</p>
 *
 * <p>This controller provides:</p>
 * <ul>
 *   <li>CRUD operations (Create, Retrieve, Update, Delete) for Coach resources</li>
 *   <li>Paginated and non-paginated retrieval of resources</li>
 *   <li>HATEOAS-compliant representations (EntityModel, CollectionModel, PagedModel)</li>
 * </ul>
 */

@RestController
@RequestMapping("/api/coaches")
public class CoachControllerImpl implements CoachController {

    private final CoachService coachService;
    private final CoachAssembler coachAssembler;
    private final CoachMapper coachMapper;

    /**
     * Constructor for injecting required dependencies.
     *
     * @param coachService   the service layer for Coach entities
     * @param coachAssembler the HATEOAS assembler for Coach entities
     * @param coachMapper    the mapper for converting between Coach and its DTOs
     */
    public CoachControllerImpl(CoachService coachService, CoachAssembler coachAssembler, CoachMapper coachMapper) {
        this.coachService = coachService;
        this.coachAssembler = coachAssembler;
        this.coachMapper = coachMapper;
    }

    @Override
    public ResponseEntity<EntityModel<CoachDTOResponse>> createCoach(@Valid @RequestBody CoachDTORequest coachDTO) {
        Coach coach = coachService.createCoach(coachMapper.toEntity(coachDTO));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(coachAssembler.toModel(coach));
    }


    @Override
    public ResponseEntity<EntityModel<CoachDTOResponse>> getCoachById(@PathVariable Long id) {
        Coach coach = coachService.getCoachById(id);
        return ResponseEntity.ok(coachAssembler.toModel(coach));
    }


    @Override
    public ResponseEntity<PagedModel<EntityModel<CoachDTOResponse>>> getCoaches(@ParameterObject Pageable pageable) {
        Page<Coach> coachPage = coachService.getCoaches(pageable);
        return ResponseEntity.ok(coachAssembler.toPagedModel(coachPage));
    }


    @Override
    public ResponseEntity<CollectionModel<EntityModel<CoachDTOResponse>>> getAllCoaches() {
        List<Coach> coaches = coachService.getAllCoaches();
        return ResponseEntity.ok(coachAssembler.toCollectionModel(coaches));
    }


    @Override
    public ResponseEntity<EntityModel<CoachDTOResponse>> updateCoach(@PathVariable Long id, @Valid @RequestBody CoachDTORequest updatedCoachDTO) {
        Coach updatedCoach = coachService.updateCoach(id, coachMapper.toEntity(updatedCoachDTO));
        return ResponseEntity.ok(coachAssembler.toModel(updatedCoach));
    }


    @Override
    public ResponseEntity<Void> deleteCoach(@PathVariable Long id) {
        coachService.deleteCoach(id);
        return ResponseEntity.noContent().build();
    }
}