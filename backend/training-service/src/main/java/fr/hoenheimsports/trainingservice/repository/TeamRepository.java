package fr.hoenheimsports.trainingservice.repository;

import fr.hoenheimsports.trainingservice.model.Category;
import fr.hoenheimsports.trainingservice.model.Gender;
import fr.hoenheimsports.trainingservice.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

public interface TeamRepository extends JpaRepository<Team, Long> {
    boolean existsByGenderAndCategoryAndTeamNumber(@NonNull Gender gender, @NonNull Category category, @NonNull int teamNumber);
}