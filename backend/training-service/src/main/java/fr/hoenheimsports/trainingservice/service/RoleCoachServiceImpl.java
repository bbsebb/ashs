package fr.hoenheimsports.trainingservice.service;

import fr.hoenheimsports.trainingservice.model.Coach;
import fr.hoenheimsports.trainingservice.model.Role;
import fr.hoenheimsports.trainingservice.model.RoleCoach;
import fr.hoenheimsports.trainingservice.model.Team;
import fr.hoenheimsports.trainingservice.repository.RoleCoachRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class RoleCoachServiceImpl implements RoleCoachService {

    private final RoleCoachRepository roleCoachRepository;

    public RoleCoachServiceImpl(RoleCoachRepository roleCoachRepository) {
        this.roleCoachRepository = roleCoachRepository;
    }

    @Override
    public RoleCoach getRoleCoachById(long id) {
        log.debug("Recherche du rôle de coach avec l'ID: {}", id);
        RoleCoach roleCoach = roleCoachRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Rôle de coach introuvable avec l'ID: {}", id);
                    return new EntityNotFoundException("RoleCoach introuvable avec l'id : " + id);
                });
        log.debug("Rôle de coach trouvé: ID={}, rôle={}", roleCoach.getId(), roleCoach.getRole());
        return roleCoach;
    }

    @Override
    public RoleCoach createRoleCoach(Role role, Coach coach, Team team) {
        log.info("Création d'un nouveau rôle de coach: rôle={}, coach={}, équipe={}/{}/{}",
                role, coach.getName() + " " + coach.getSurname(), 
                team.getGender(), team.getCategory(), team.getTeamNumber());

        RoleCoach roleCoach = new RoleCoach();
        roleCoach.setRole(role);
        log.debug("Association du rôle {} au coach", role);
        team.addRoleCoach(roleCoach);
        log.debug("Association du rôle à l'équipe: {}/{}/{}", 
                team.getGender(), team.getCategory(), team.getTeamNumber());
        coach.addRoleCoach(roleCoach);
        log.debug("Association du rôle au coach: {} {}", coach.getName(), coach.getSurname());

        RoleCoach savedRoleCoach = roleCoachRepository.save(roleCoach);
        log.info("Rôle de coach créé avec succès, ID: {}", savedRoleCoach.getId());
        return savedRoleCoach;
    }

    @Override
    @Transactional
    public void deleteRoleCoach(long roleCoachId) {
        log.info("Suppression du rôle de coach avec l'ID: {}", roleCoachId);

        roleCoachRepository.findById(roleCoachId)
                .ifPresentOrElse(roleCoach -> {
                    log.debug("Rôle de coach trouvé, suppression des associations");
                    Team team = roleCoach.getTeam();
                    Coach coach = roleCoach.getCoach();

                    log.debug("Suppression de l'association avec l'équipe: {}/{}/{}", 
                            team.getGender(), team.getCategory(), team.getTeamNumber());
                    team.removeRoleCoach(roleCoach);

                    log.debug("Suppression de l'association avec le coach: {} {}", 
                            coach.getName(), coach.getSurname());
                    coach.removeRoleCoach(roleCoach);

                    roleCoachRepository.delete(roleCoach);
                    log.info("Rôle de coach supprimé avec succès, ID: {}", roleCoachId);
                }, () -> {
                    log.warn("Tentative de suppression d'un rôle de coach inexistant, ID: {}", roleCoachId);
                    throw new EntityNotFoundException("RoleCoach not found");
                });
    }
}
