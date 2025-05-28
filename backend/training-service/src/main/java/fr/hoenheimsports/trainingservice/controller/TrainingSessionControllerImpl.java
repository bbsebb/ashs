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

@RestController
@RequestMapping("/api/training-sessions")
public class TrainingSessionControllerImpl implements TrainingSessionController {

    private final TrainingSessionService trainingSessionService;
    private final TrainingSessionAssembler trainingSessionAssembler;
    private final TrainingSessionMapper trainingSessionMapper;

    public TrainingSessionControllerImpl(TrainingSessionService trainingSessionService,
                                         TrainingSessionAssembler trainingSessionAssembler, TrainingSessionMapper trainingSessionMapper) {
        this.trainingSessionService = trainingSessionService;
        this.trainingSessionAssembler = trainingSessionAssembler;
        this.trainingSessionMapper = trainingSessionMapper;
    }

    @Override
    public ResponseEntity<EntityModel<TrainingSessionDTOResponse>> createTrainingSession(
            @Valid @RequestBody TrainingSessionDTORequest trainingSessionDTO) {
        var trainingSession = trainingSessionService.createTrainingSession(trainingSessionMapper.toEntity(trainingSessionDTO));
        return new ResponseEntity<>(trainingSessionAssembler.toModel(trainingSession), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<EntityModel<TrainingSessionDTOResponse>> getTrainingSessionById(@PathVariable Long id) {
        var trainingSession = trainingSessionService.getTrainingSessionById(id);
        return ResponseEntity.ok(trainingSessionAssembler.toModel(trainingSession));
    }

    @Override
    public ResponseEntity<PagedModel<EntityModel<TrainingSessionDTOResponse>>> getTrainingSessions(
            @ParameterObject Pageable pageable) {
        Page<TrainingSession> trainingSessionsPage = trainingSessionService.getTrainingSessions(pageable);
        return ResponseEntity.ok(trainingSessionAssembler.toPagedModel(trainingSessionsPage));
    }

    @Override
    public ResponseEntity<EntityModel<TrainingSessionDTOResponse>> updateTrainingSession(
            @PathVariable Long id,
            @Valid @RequestBody TrainingSessionDTORequest trainingSessionDTO) {
        var updatedTrainingSession = trainingSessionService.updateTrainingSession(id, trainingSessionMapper.toEntity(trainingSessionDTO));
        return ResponseEntity.ok(trainingSessionAssembler.toModel(updatedTrainingSession));
    }

    @Override
    public ResponseEntity<Void> deleteTrainingSession(@PathVariable Long id) {
        trainingSessionService.deleteTrainingSession(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<CollectionModel<EntityModel<TrainingSessionDTOResponse>>> getAllTrainingSessions() {
        List<TrainingSession> trainingSessions = trainingSessionService.getAllTrainingSessions();
        return ResponseEntity.ok(trainingSessionAssembler.toCollectionModel(trainingSessions));
    }

}
