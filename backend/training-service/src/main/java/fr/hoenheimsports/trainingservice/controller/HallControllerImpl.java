package fr.hoenheimsports.trainingservice.controller;

import fr.hoenheimsports.trainingservice.assembler.HallAssembler;
import fr.hoenheimsports.trainingservice.dto.request.HallDTOCreateRequest;
import fr.hoenheimsports.trainingservice.dto.request.HallDTOUpdateRequest;
import fr.hoenheimsports.trainingservice.dto.response.HallDTOResponse;
import fr.hoenheimsports.trainingservice.mapper.HallMapper;
import fr.hoenheimsports.trainingservice.model.Hall;
import fr.hoenheimsports.trainingservice.service.HallService;
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
 * Implementation of the HallController interface for handling hall operations.
 * 
 * <p>This controller provides REST API endpoints for creating, retrieving, updating, and deleting
 * hall resources. It delegates the actual business logic to the HallService and uses
 * HallAssembler to convert the domain entities to HATEOAS-enabled DTOs with appropriate links.</p>
 * 
 * <p>The controller logs all requests and responses for monitoring purposes.</p>
 * 
 * @since 1.0
 */

@RestController
@RequestMapping("/api/halls")
@Slf4j
public class HallControllerImpl implements HallController {
    /**
     * The service used for hall operations.
     */
    private final HallService hallService;

    /**
     * The assembler used to convert hall entities to DTOs with HATEOAS links.
     */
    private final HallAssembler hallAssembler;

    /**
     * The mapper used to convert between hall DTOs and entities.
     */
    private final HallMapper hallMapper;

    /**
     * Constructs a new HallControllerImpl with the specified dependencies.
     * 
     * @param hallService The service to use for hall operations
     * @param hallAssembler The assembler to use for converting hall entities to DTOs with HATEOAS links
     * @param hallMapper The mapper to use for converting between hall DTOs and entities
     */
    public HallControllerImpl(HallService hallService, HallAssembler hallAssembler, HallMapper hallMapper) {
        this.hallService = hallService;
        this.hallAssembler = hallAssembler;
        this.hallMapper = hallMapper;
    }


    @Override
    public ResponseEntity<EntityModel<HallDTOResponse>> createHall(@Valid @RequestBody HallDTOCreateRequest hallDTO) {
        log.info("Réception d'une requête de création de salle: {}", hallDTO.name());
        log.debug("Détails de la salle à créer: adresse={}, {}, {}, {}", 
                hallDTO.address().street(), hallDTO.address().postalCode(), 
                hallDTO.address().city(), hallDTO.address().country());
        Hall hall = hallService.createHall(hallMapper.toEntity(hallDTO));
        log.info("Salle créée avec succès, ID: {}", hall.getId());
        return new ResponseEntity<>(this.hallAssembler.toModel(hall), HttpStatus.CREATED);
    }


    @Override
    public ResponseEntity<EntityModel<HallDTOResponse>> getHallById(@PathVariable Long id) {
        log.info("Réception d'une requête pour obtenir la salle avec l'ID: {}", id);
        Hall hall = hallService.getHallById(id);
        log.info("Salle trouvée et renvoyée: {}", hall.getName());
        return ResponseEntity.ok(this.hallAssembler.toModel(hall));
    }


    @Override
    public ResponseEntity<PagedModel<EntityModel<HallDTOResponse>>> getHalls(@ParameterObject Pageable pageable) {
        log.info("Réception d'une requête pour obtenir les salles paginées");
        log.debug("Paramètres de pagination: page={}, taille={}, tri={}", 
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        Page<Hall> pagedHalls = hallService.getHalls(pageable);
        log.info("Retour de {} salles paginées", pagedHalls.getTotalElements());
        return ResponseEntity.ok(this.hallAssembler.toPagedModel(pagedHalls));
    }


    @Override
    public ResponseEntity<EntityModel<HallDTOResponse>> updateHall(@PathVariable Long id, @Valid @RequestBody HallDTOUpdateRequest updatedHallDTO) {
        log.info("Réception d'une requête de mise à jour de la salle avec l'ID: {}", id);
        log.debug("Nouvelles informations: nom={}, adresse={}, {}, {}, {}", 
                updatedHallDTO.name(), updatedHallDTO.address().street(), 
                updatedHallDTO.address().postalCode(), updatedHallDTO.address().city(), 
                updatedHallDTO.address().country());
        Hall persistedHall = hallService.updateHall(id, hallMapper.toEntity(updatedHallDTO));
        log.info("Salle mise à jour avec succès, ID: {}", persistedHall.getId());
        return ResponseEntity.ok(this.hallAssembler.toModel(persistedHall));
    }


    @Override
    public ResponseEntity<Void> deleteHall(@PathVariable Long id) {
        log.info("Réception d'une requête de suppression de la salle avec l'ID: {}", id);
        hallService.deleteHall(id);
        log.info("Salle supprimée avec succès, ID: {}", id);
        return ResponseEntity.noContent().build();
    }


    @Override
    public ResponseEntity<CollectionModel<EntityModel<HallDTOResponse>>> getAllHalls() {
        log.info("Réception d'une requête pour obtenir toutes les salles");
        List<Hall> halls = hallService.getAllHalls();
        log.info("Retour de {} salles au total", halls.size());
        return ResponseEntity.ok(this.hallAssembler.toCollectionModel(halls));
    }


}
