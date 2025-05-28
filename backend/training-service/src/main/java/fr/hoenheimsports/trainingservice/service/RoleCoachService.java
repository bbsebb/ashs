package fr.hoenheimsports.trainingservice.service;

import fr.hoenheimsports.trainingservice.model.Coach;
import fr.hoenheimsports.trainingservice.model.Role;
import fr.hoenheimsports.trainingservice.model.RoleCoach;
import fr.hoenheimsports.trainingservice.model.Team;

public interface RoleCoachService {
    RoleCoach getRoleCoachById(long id);

    RoleCoach createRoleCoach(Role role, Coach coach, Team team);

    void deleteRoleCoach(long roleCoachId);
}
