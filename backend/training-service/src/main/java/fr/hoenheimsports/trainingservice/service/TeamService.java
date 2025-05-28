package fr.hoenheimsports.trainingservice.service;

import fr.hoenheimsports.trainingservice.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TeamService {
    Team createTeam(Team team);

    Team getTeamById(Long id);

    Page<Team> getTeams(Pageable pageable);

    List<Team> getTeams();

    Team updateTeam(Long id, Team updatedTeam);

    TrainingSession addTrainingSession(Long teamId, Long hallId, TrainingSession trainingSession);

    RoleCoach addRoleCoach(Long teamId, Long coachId, Role role);

    void deleteTeam(Long id);

    boolean isNotUniqueTeam(Gender gender, Category category, int teamNumber);
}
