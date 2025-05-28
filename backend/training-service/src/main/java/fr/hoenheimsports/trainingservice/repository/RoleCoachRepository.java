package fr.hoenheimsports.trainingservice.repository;

import fr.hoenheimsports.trainingservice.model.Role;
import fr.hoenheimsports.trainingservice.model.RoleCoach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface RoleCoachRepository extends JpaRepository<RoleCoach, Long> {




    Optional<RoleCoach> findByCoach_IdAndTeam_IdAndRole(@NonNull Long id, @NonNull Long id1, @NonNull Role role);
}