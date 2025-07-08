package fr.hoenheimsports.trainingservice.controller;

import fr.hoenheimsports.trainingservice.assembler.RoleCoachAssembler;
import fr.hoenheimsports.trainingservice.assembler.TeamAssembler;
import fr.hoenheimsports.trainingservice.assembler.TrainingSessionAssembler;
import fr.hoenheimsports.trainingservice.dto.request.AddCoachInTeamDTORequest;
import fr.hoenheimsports.trainingservice.dto.request.AddTrainingSessionInTeamDTORequest;
import fr.hoenheimsports.trainingservice.dto.request.TeamDTOCreateRequest;
import fr.hoenheimsports.trainingservice.dto.request.TeamDTOUpdateRequest;
import fr.hoenheimsports.trainingservice.dto.response.RoleCoachDTOResponse;
import fr.hoenheimsports.trainingservice.dto.response.TeamDTOResponse;
import fr.hoenheimsports.trainingservice.dto.response.TrainingSessionDTOResponse;
import fr.hoenheimsports.trainingservice.mapper.TeamMapper;
import fr.hoenheimsports.trainingservice.mapper.TrainingSessionMapper;
import fr.hoenheimsports.trainingservice.model.Role;
import fr.hoenheimsports.trainingservice.model.RoleCoach;
import fr.hoenheimsports.trainingservice.model.Team;
import fr.hoenheimsports.trainingservice.model.TrainingSession;
import fr.hoenheimsports.trainingservice.service.TeamService;
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
 * Implementation of the TeamController interface for handling team operations.
 * 
 * <p>This controller provides REST API endpoints for creating, retrieving, updating, and deleting
 * team resources, as well as adding training sessions and coaches to teams. It delegates the actual
 * business logic to the TeamService and uses various assemblers to convert the domain entities
 * to HATEOAS-enabled DTOs with appropriate links.</p>
 * 
 * <p>The controller logs all requests and responses for monitoring purposes.</p>
 * 
 * @since 1.0
 */
@RestController
@RequestMapping("/api/teams")
@Slf4j
public class TeamControllerImpl implements TeamController {

    /**
     * The service used for team operations.
     */
    private final TeamService teamService;

    /**
     * The assembler used to convert team entities to DTOs with HATEOAS links.
     */
    private final TeamAssembler teamAssembler;

    /**
     * The mapper used to convert between team DTOs and entities.
     */
    private final TeamMapper teamMapper;

    /**
     * The mapper used to convert between training session DTOs and entities.
     */
    private final TrainingSessionMapper trainingSessionMapper;

    /**
     * The assembler used to convert training session entities to DTOs with HATEOAS links.
     */
    private final TrainingSessionAssembler trainingSessionAssembler;

    /**
     * The assembler used to convert role coach entities to DTOs with HATEOAS links.
     */
    private final RoleCoachAssembler roleCoachAssembler;

    /**
     * Constructs a new TeamControllerImpl with the specified dependencies.
     * 
     * @param teamService The service to use for team operations
     * @param teamAssembler The assembler to use for converting team entities to DTOs with HATEOAS links
     * @param teamMapper The mapper to use for converting between team DTOs and entities
     * @param trainingSessionMapper The mapper to use for converting between training session DTOs and entities
     * @param trainingSessionAssembler The assembler to use for converting training session entities to DTOs with HATEOAS links
     * @param roleCoachAssembler The assembler to use for converting role coach entities to DTOs with HATEOAS links
     */
    public TeamControllerImpl(TeamService teamService, TeamAssembler teamAssembler, TeamMapper teamMapper, TrainingSessionMapper trainingSessionMapper, TrainingSessionAssembler trainingSessionAssembler, RoleCoachAssembler roleCoachAssembler) {
        this.teamService = teamService;
        this.teamAssembler = teamAssembler;
        this.teamMapper = teamMapper;
        this.trainingSessionMapper = trainingSessionMapper;
        this.trainingSessionAssembler = trainingSessionAssembler;
        this.roleCoachAssembler = roleCoachAssembler;
    }

    @Override
    public ResponseEntity<EntityModel<TeamDTOResponse>> createTeam(@Valid @RequestBody TeamDTOCreateRequest teamDTO) {
        log.info("Réception d'une requête de création d'équipe: {}/{}/{}", 
                teamDTO.gender(), teamDTO.category(), teamDTO.teamNumber());
        Team team = teamMapper.toEntity(teamDTO);
        Team savedTeam = teamService.createTeam(team);
        log.info("Équipe créée avec succès, ID: {}", savedTeam.getId());
        EntityModel<TeamDTOResponse> response = teamAssembler.toModel(savedTeam);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<EntityModel<TeamDTOResponse>> getTeamById(@PathVariable long id) {
        log.info("Réception d'une requête pour obtenir l'équipe avec l'ID: {}", id);
        Team team = teamService.getTeamById(id);
        log.info("Équipe trouvée et renvoyée: {}/{}/{}", 
                team.getGender(), team.getCategory(), team.getTeamNumber());
        EntityModel<TeamDTOResponse> response = teamAssembler.toModel(team);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<PagedModel<EntityModel<TeamDTOResponse>>> getTeams(@ParameterObject Pageable pageable) {
        log.info("Réception d'une requête pour obtenir les équipes paginées");
        log.debug("Paramètres de pagination: page={}, taille={}, tri={}", 
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        Page<Team> teams = teamService.getTeams(pageable);
        log.info("Retour de {} équipes paginées", teams.getTotalElements());
        PagedModel<EntityModel<TeamDTOResponse>> response = teamAssembler.toPagedModel(teams);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<CollectionModel<EntityModel<TeamDTOResponse>>> getAllTeams() {
        log.info("Réception d'une requête pour obtenir toutes les équipes");
        List<Team> teams = teamService.getTeams();
        log.info("Retour de {} équipes au total", teams.size());
        CollectionModel<EntityModel<TeamDTOResponse>> response = teamAssembler.toCollectionModel(teams);
        return ResponseEntity.ok(response);
    }


    @Override
    public ResponseEntity<EntityModel<TeamDTOResponse>> updateTeam(@PathVariable long id, @Valid @RequestBody TeamDTOUpdateRequest updatedTeamDTO) {
        log.info("Réception d'une requête de mise à jour de l'équipe avec l'ID: {}", id);
        log.debug("Nouvelles informations: genre={}, catégorie={}, numéro={}", 
                updatedTeamDTO.gender(), updatedTeamDTO.category(), updatedTeamDTO.teamNumber());
        Team updatedTeam = teamMapper.toEntity(updatedTeamDTO);
        Team savedTeam = teamService.updateTeam(id, updatedTeam);
        log.info("Équipe mise à jour avec succès, ID: {}", savedTeam.getId());
        EntityModel<TeamDTOResponse> response = teamAssembler.toModel(savedTeam);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> deleteTeam(@PathVariable long id) {
        log.info("Réception d'une requête de suppression de l'équipe avec l'ID: {}", id);
        teamService.deleteTeam(id);
        log.info("Équipe supprimée avec succès, ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<EntityModel<TrainingSessionDTOResponse>> addTrainingSession(@PathVariable long teamId, @Valid @RequestBody AddTrainingSessionInTeamDTORequest addTrainingSessionInTeamDTORequest) {
        log.info("Réception d'une requête d'ajout de séance d'entraînement à l'équipe ID: {}", teamId);
        log.debug("Détails de la requête: salle ID={}, jour={}, heure de début={}, heure de fin={}", 
                addTrainingSessionInTeamDTORequest.hallId(),
                addTrainingSessionInTeamDTORequest.trainingSessionDTORequest().timeSlot().dayOfWeek(),
                addTrainingSessionInTeamDTORequest.trainingSessionDTORequest().timeSlot().startTime(),
                addTrainingSessionInTeamDTORequest.trainingSessionDTORequest().timeSlot().endTime());

        TrainingSession trainingSession = trainingSessionMapper.toEntity(addTrainingSessionInTeamDTORequest.trainingSessionDTORequest());
        trainingSession = teamService.addTrainingSession(teamId, addTrainingSessionInTeamDTORequest.hallId(), trainingSession);

        log.info("Séance d'entraînement ajoutée avec succès à l'équipe ID: {}, séance ID: {}", teamId, trainingSession.getId());
        EntityModel<TrainingSessionDTOResponse> response = trainingSessionAssembler.toModel(trainingSession);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @Override
    public ResponseEntity<EntityModel<RoleCoachDTOResponse>> addRoleCoach(@PathVariable long teamId, @Valid @RequestBody AddCoachInTeamDTORequest addCoachInTeamDTORequest) {
        log.info("Réception d'une requête d'ajout de rôle de coach à l'équipe ID: {}", teamId);
        log.debug("Détails de la requête: coach ID={}, rôle={}", 
                addCoachInTeamDTORequest.coachId(), addCoachInTeamDTORequest.role());

        Role role = Role.valueOf(addCoachInTeamDTORequest.role());
        RoleCoach roleCoach = teamService.addRoleCoach(teamId, addCoachInTeamDTORequest.coachId(), role);

        log.info("Rôle de coach ajouté avec succès à l'équipe ID: {}, rôle ID: {}", teamId, roleCoach.getId());
        EntityModel<RoleCoachDTOResponse> response = roleCoachAssembler.toModel(roleCoach);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


}
