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
@RequestMapping("/api/teams")
public class TeamControllerImpl implements TeamController {

    private final TeamService teamService;
    private final TeamAssembler teamAssembler;
    private final TeamMapper teamMapper;
    private final TrainingSessionMapper trainingSessionMapper;
    private final TrainingSessionAssembler trainingSessionAssembler;
    private final RoleCoachAssembler roleCoachAssembler;

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
        Team team = teamMapper.toEntity(teamDTO);
        Team savedTeam = teamService.createTeam(team);
        EntityModel<TeamDTOResponse> response = teamAssembler.toModel(savedTeam);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<EntityModel<TeamDTOResponse>> getTeamById(@PathVariable long id) {
        Team team = teamService.getTeamById(id);
        EntityModel<TeamDTOResponse> response = teamAssembler.toModel(team);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<PagedModel<EntityModel<TeamDTOResponse>>> getTeams(@ParameterObject Pageable pageable) {
        Page<Team> teams = teamService.getTeams(pageable);
        PagedModel<EntityModel<TeamDTOResponse>> response = teamAssembler.toPagedModel(teams);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<CollectionModel<EntityModel<TeamDTOResponse>>> getAllTeams() {
        List<Team> teams = teamService.getTeams();
        CollectionModel<EntityModel<TeamDTOResponse>> response = teamAssembler.toCollectionModel(teams);
        return ResponseEntity.ok(response);
    }


    @Override
    public ResponseEntity<EntityModel<TeamDTOResponse>> updateTeam(@PathVariable long id, @Valid @RequestBody TeamDTOUpdateRequest updatedTeamDTO) {
        Team updatedTeam = teamMapper.toEntity(updatedTeamDTO);
        Team savedTeam = teamService.updateTeam(id, updatedTeam);
        EntityModel<TeamDTOResponse> response = teamAssembler.toModel(savedTeam);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> deleteTeam(@PathVariable long id) {
        teamService.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<EntityModel<TrainingSessionDTOResponse>> addTrainingSession(@PathVariable long teamId, @Valid @RequestBody AddTrainingSessionInTeamDTORequest addTrainingSessionInTeamDTORequest) {
        TrainingSession trainingSession = trainingSessionMapper.toEntity(addTrainingSessionInTeamDTORequest.trainingSessionDTORequest());
        trainingSession = teamService.addTrainingSession(teamId, addTrainingSessionInTeamDTORequest.hallId(), trainingSession);
        EntityModel<TrainingSessionDTOResponse> response = trainingSessionAssembler.toModel(trainingSession);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @Override
    public ResponseEntity<EntityModel<RoleCoachDTOResponse>> addRoleCoach(@PathVariable long teamId, @Valid @RequestBody AddCoachInTeamDTORequest addCoachInTeamDTORequest) {
        Role role = Role.valueOf(addCoachInTeamDTORequest.role());
        RoleCoach roleCoach = teamService.addRoleCoach(teamId, addCoachInTeamDTORequest.coachId(), role);
        EntityModel<RoleCoachDTOResponse> response = roleCoachAssembler.toModel(roleCoach);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


}
