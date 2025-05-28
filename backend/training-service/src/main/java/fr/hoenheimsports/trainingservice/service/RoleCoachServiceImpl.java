package fr.hoenheimsports.trainingservice.service;

import fr.hoenheimsports.trainingservice.model.Coach;
import fr.hoenheimsports.trainingservice.model.Role;
import fr.hoenheimsports.trainingservice.model.RoleCoach;
import fr.hoenheimsports.trainingservice.model.Team;
import fr.hoenheimsports.trainingservice.repository.RoleCoachRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoleCoachServiceImpl implements RoleCoachService {

    private final RoleCoachRepository roleCoachRepository;

    public RoleCoachServiceImpl(RoleCoachRepository roleCoachRepository) {
        this.roleCoachRepository = roleCoachRepository;
    }

    @Override
    public RoleCoach getRoleCoachById(long id) {
        return roleCoachRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("RoleCoach introuvable avec l'id : " + id));
    }

    @Override
    public RoleCoach createRoleCoach(Role role, Coach coach, Team team) {
        RoleCoach roleCoach = new RoleCoach();
        roleCoach.setRole(role);
        team.addRoleCoach(roleCoach);
        coach.addRoleCoach(roleCoach);
        return roleCoachRepository.save(roleCoach);
    }

    @Override
    @Transactional
    public void deleteRoleCoach(long roleCoachId) {
        roleCoachRepository.findById(roleCoachId)
                .ifPresentOrElse(roleCoach -> {
                    roleCoach.getTeam().removeRoleCoach(roleCoach);
                    roleCoach.getCoach().removeRoleCoach(roleCoach);
                    roleCoachRepository.delete(roleCoach);
                }, () -> {
                    throw new EntityNotFoundException("RoleCoach not found");
                });
    }
}
