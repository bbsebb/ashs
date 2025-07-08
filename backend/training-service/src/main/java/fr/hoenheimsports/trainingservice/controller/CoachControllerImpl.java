package fr.hoenheimsports.trainingservice.controller;

import fr.hoenheimsports.trainingservice.assembler.CoachAssembler;
import fr.hoenheimsports.trainingservice.dto.request.CoachDTORequest;
import fr.hoenheimsports.trainingservice.dto.response.CoachDTOResponse;
import fr.hoenheimsports.trainingservice.mapper.CoachMapper;
import fr.hoenheimsports.trainingservice.model.Coach;
import fr.hoenheimsports.trainingservice.service.CoachService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
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
 * Implementation of the CoachController interface for handling coach operations.
 * 
 * <p>This controller provides REST API endpoints for creating, retrieving, updating, and deleting
 * coach resources. It delegates the actual business logic to the CoachService and uses
 * CoachAssembler to convert the domain entities to HATEOAS-enabled DTOs with appropriate links.</p>
 * 
 * <p>The controller logs all requests and responses for monitoring purposes.</p>
 * 
 * @since 1.0
 */

@RestController
@RequestMapping("/api/coaches")
@Slf4j
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
        log.info("Réception d'une requête de création de coach: {} {}", coachDTO.name(), coachDTO.surname());
        log.debug("Détails du coach à créer: email={}, téléphone={}", coachDTO.email(), coachDTO.phone());
        Coach coach = coachService.createCoach(coachMapper.toEntity(coachDTO));
        log.info("Coach créé avec succès, ID: {}", coach.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(coachAssembler.toModel(coach));
    }


    @Override
    public ResponseEntity<EntityModel<CoachDTOResponse>> getCoachById(@PathVariable Long id) {
        log.info("Réception d'une requête pour obtenir le coach avec l'ID: {}", id);
        Coach coach = coachService.getCoachById(id);
        log.info("Coach trouvé et renvoyé: {} {}", coach.getName(), coach.getSurname());
        return ResponseEntity.ok(coachAssembler.toModel(coach));
    }


    @Override
    public ResponseEntity<PagedModel<EntityModel<CoachDTOResponse>>> getCoaches(@ParameterObject Pageable pageable) {
        log.info("Réception d'une requête pour obtenir les coachs paginés");
        log.debug("Paramètres de pagination: page={}, taille={}, tri={}", 
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        Page<Coach> coachPage = coachService.getCoaches(pageable);
        log.info("Retour de {} coachs paginés", coachPage.getTotalElements());
        return ResponseEntity.ok(coachAssembler.toPagedModel(coachPage));
    }


    @Override
    public ResponseEntity<CollectionModel<EntityModel<CoachDTOResponse>>> getAllCoaches() {
        log.info("Réception d'une requête pour obtenir tous les coachs");
        List<Coach> coaches = coachService.getAllCoaches();
        log.info("Retour de {} coachs au total", coaches.size());
        return ResponseEntity.ok(coachAssembler.toCollectionModel(coaches));
    }


    @Override
    public ResponseEntity<EntityModel<CoachDTOResponse>> updateCoach(@PathVariable Long id, @Valid @RequestBody CoachDTORequest updatedCoachDTO) {
        log.info("Réception d'une requête de mise à jour du coach avec l'ID: {}", id);
        log.debug("Nouvelles informations: nom={}, prénom={}, email={}, téléphone={}", 
                updatedCoachDTO.name(), updatedCoachDTO.surname(), 
                updatedCoachDTO.email(), updatedCoachDTO.phone());
        Coach updatedCoach = coachService.updateCoach(id, coachMapper.toEntity(updatedCoachDTO));
        log.info("Coach mis à jour avec succès, ID: {}", updatedCoach.getId());
        return ResponseEntity.ok(coachAssembler.toModel(updatedCoach));
    }


    @Override
    public ResponseEntity<Void> deleteCoach(@PathVariable Long id) {
        log.info("Réception d'une requête de suppression du coach avec l'ID: {}", id);
        coachService.deleteCoach(id);
        log.info("Coach supprimé avec succès, ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}
