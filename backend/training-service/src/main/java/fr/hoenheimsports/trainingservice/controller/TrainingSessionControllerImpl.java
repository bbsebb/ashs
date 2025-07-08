package fr.hoenheimsports.trainingservice.controller;

import fr.hoenheimsports.trainingservice.assembler.TeamAssembler;
import fr.hoenheimsports.trainingservice.assembler.TrainingSessionAssembler;
import fr.hoenheimsports.trainingservice.dto.request.TrainingSessionDTORequest;
import fr.hoenheimsports.trainingservice.dto.response.TrainingSessionDTOResponse;
import fr.hoenheimsports.trainingservice.mapper.TrainingSessionMapper;
import fr.hoenheimsports.trainingservice.model.TrainingSession;
import fr.hoenheimsports.trainingservice.service.TeamService;
import fr.hoenheimsports.trainingservice.service.TrainingSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Implementation of the TrainingSessionController interface for handling training session operations.
 * 
 * <p>This controller provides REST API endpoints for creating, retrieving, updating, and deleting
 * training session resources. It delegates the actual business logic to the TrainingSessionService
 * and uses TrainingSessionAssembler to convert the domain entities to HATEOAS-enabled DTOs with
 * appropriate links.</p>
 * 
 * <p>The controller logs all requests and responses for monitoring purposes.</p>
 * 
 * @since 1.0
 */
@RestController
@RequestMapping("/api/training-sessions")
@Slf4j
public class TrainingSessionControllerImpl implements TrainingSessionController {

    /**
     * The service used for training session operations.
     */
    private final TrainingSessionService trainingSessionService;

    /**
     * The assembler used to convert training session entities to DTOs with HATEOAS links.
     */
    private final TrainingSessionAssembler trainingSessionAssembler;

    /**
     * The mapper used to convert between training session DTOs and entities.
     */
    private final TrainingSessionMapper trainingSessionMapper;

    /**
     * Constructs a new TrainingSessionControllerImpl with the specified dependencies.
     * 
     * @param trainingSessionService The service to use for training session operations
     * @param trainingSessionAssembler The assembler to use for converting training session entities to DTOs with HATEOAS links
     * @param trainingSessionMapper The mapper to use for converting between training session DTOs and entities
     */
    public TrainingSessionControllerImpl(TrainingSessionService trainingSessionService,
                                         TrainingSessionAssembler trainingSessionAssembler, TrainingSessionMapper trainingSessionMapper) {
        this.trainingSessionService = trainingSessionService;
        this.trainingSessionAssembler = trainingSessionAssembler;
        this.trainingSessionMapper = trainingSessionMapper;
    }

    @Override
    public ResponseEntity<EntityModel<TrainingSessionDTOResponse>> createTrainingSession(
            @Valid @RequestBody TrainingSessionDTORequest trainingSessionDTO) {
        log.info("Réception d'une requête de création de séance d'entraînement");
        log.debug("Détails de la séance: jour={}, heure de début={}, heure de fin={}", 
                trainingSessionDTO.timeSlot().dayOfWeek(), 
                trainingSessionDTO.timeSlot().startTime(), 
                trainingSessionDTO.timeSlot().endTime());

        var trainingSession = trainingSessionService.createTrainingSession(trainingSessionMapper.toEntity(trainingSessionDTO));
        log.info("Séance d'entraînement créée avec succès, ID: {}", trainingSession.getId());

        return new ResponseEntity<>(trainingSessionAssembler.toModel(trainingSession), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<EntityModel<TrainingSessionDTOResponse>> getTrainingSessionById(@PathVariable Long id) {
        log.info("Réception d'une requête pour obtenir la séance d'entraînement avec l'ID: {}", id);
        var trainingSession = trainingSessionService.getTrainingSessionById(id);

        log.info("Séance d'entraînement trouvée et renvoyée: jour={}, heure de début={}, heure de fin={}", 
                trainingSession.getTimeSlot().getDayOfWeek(), 
                trainingSession.getTimeSlot().getStartTime(), 
                trainingSession.getTimeSlot().getEndTime());

        return ResponseEntity.ok(trainingSessionAssembler.toModel(trainingSession));
    }

    @Override
    public ResponseEntity<PagedModel<EntityModel<TrainingSessionDTOResponse>>> getTrainingSessions(
            @ParameterObject Pageable pageable) {
        log.info("Réception d'une requête pour obtenir les séances d'entraînement paginées");
        log.debug("Paramètres de pagination: page={}, taille={}, tri={}", 
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

        Page<TrainingSession> trainingSessionsPage = trainingSessionService.getTrainingSessions(pageable);
        log.info("Retour de {} séances d'entraînement paginées", trainingSessionsPage.getTotalElements());

        return ResponseEntity.ok(trainingSessionAssembler.toPagedModel(trainingSessionsPage));
    }

    @Override
    public ResponseEntity<EntityModel<TrainingSessionDTOResponse>> updateTrainingSession(
            @PathVariable Long id,
            @Valid @RequestBody TrainingSessionDTORequest trainingSessionDTO) {
        log.info("Réception d'une requête de mise à jour de la séance d'entraînement avec l'ID: {}", id);
        log.debug("Nouvelles informations: jour={}, heure de début={}, heure de fin={}", 
                trainingSessionDTO.timeSlot().dayOfWeek(), 
                trainingSessionDTO.timeSlot().startTime(), 
                trainingSessionDTO.timeSlot().endTime());

        var updatedTrainingSession = trainingSessionService.updateTrainingSession(id, trainingSessionMapper.toEntity(trainingSessionDTO));
        log.info("Séance d'entraînement mise à jour avec succès, ID: {}", updatedTrainingSession.getId());

        return ResponseEntity.ok(trainingSessionAssembler.toModel(updatedTrainingSession));
    }

    @Override
    public ResponseEntity<Void> deleteTrainingSession(@PathVariable Long id) {
        log.info("Réception d'une requête de suppression de la séance d'entraînement avec l'ID: {}", id);
        trainingSessionService.deleteTrainingSession(id);
        log.info("Séance d'entraînement supprimée avec succès, ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<CollectionModel<EntityModel<TrainingSessionDTOResponse>>> getAllTrainingSessions() {
        log.info("Réception d'une requête pour obtenir toutes les séances d'entraînement");
        List<TrainingSession> trainingSessions = trainingSessionService.getAllTrainingSessions();
        log.info("Retour de {} séances d'entraînement au total", trainingSessions.size());
        return ResponseEntity.ok(trainingSessionAssembler.toCollectionModel(trainingSessions));
    }

}
