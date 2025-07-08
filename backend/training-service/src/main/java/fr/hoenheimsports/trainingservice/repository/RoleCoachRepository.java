package fr.hoenheimsports.trainingservice.repository;

import fr.hoenheimsports.trainingservice.model.Role;
import fr.hoenheimsports.trainingservice.model.RoleCoach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

/**
 * Repository interface for managing RoleCoach entities in the database.
 * 
 * <p>This repository provides CRUD operations for RoleCoach entities,
 * allowing the application to store, retrieve, update, and delete coach-team role relationships.
 * It also provides a method to find a specific role by coach ID, team ID, and role type.</p>
 * 
 * @since 1.0
 */
public interface RoleCoachRepository extends JpaRepository<RoleCoach, Long> {
    /**
     * Finds a RoleCoach entity by coach ID, team ID, and role type.
     * 
     * <p>This method is used to retrieve the specific relationship between a coach and a team
     * with a particular role, such as MAIN, ASSISTANT, or SUPPORT_STAFF.</p>
     * 
     * @param coachId The ID of the coach, must not be null
     * @param teamId The ID of the team, must not be null
     * @param role The role type (MAIN, ASSISTANT, or SUPPORT_STAFF), must not be null
     * @return An Optional containing the RoleCoach entity if found, or an empty Optional if not found
     */
    Optional<RoleCoach> findByCoach_IdAndTeam_IdAndRole(@NonNull Long coachId, @NonNull Long teamId, @NonNull Role role);
}
