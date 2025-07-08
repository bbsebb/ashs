package fr.hoenheimsports.trainingservice.repository;

import fr.hoenheimsports.trainingservice.model.Category;
import fr.hoenheimsports.trainingservice.model.Gender;
import fr.hoenheimsports.trainingservice.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

/**
 * Repository interface for managing Team entities in the database.
 * 
 * <p>This repository provides CRUD operations for Team entities,
 * allowing the application to store, retrieve, update, and delete teams.
 * It also provides a method to check if a team with specific details already exists.</p>
 * 
 * @since 1.0
 */
public interface TeamRepository extends JpaRepository<Team, Long> {
    /**
     * Checks if a team with the given gender, category, and team number already exists in the database.
     * 
     * <p>This method is used to enforce uniqueness constraints when creating or updating teams.
     * Teams are uniquely identified by the combination of gender, category, and team number.</p>
     * 
     * @param gender The gender of the team (F, M, or N), must not be null
     * @param category The category of the team (age group or skill level), must not be null
     * @param teamNumber The number of the team within its gender and category, must be positive
     * @return true if a team with the given gender, category, and team number exists, false otherwise
     */
    boolean existsByGenderAndCategoryAndTeamNumber(@NonNull Gender gender, @NonNull Category category, @NonNull int teamNumber);
}
