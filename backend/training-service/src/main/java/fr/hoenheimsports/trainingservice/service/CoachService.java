package fr.hoenheimsports.trainingservice.service;

import fr.hoenheimsports.trainingservice.model.Coach;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CoachService {
    Coach createCoach(Coach coach);

    Coach getCoachById(Long id);

    Page<Coach> getCoaches(Pageable pageable);

    List<Coach> getAllCoaches();

    Coach updateCoach(Long id, Coach updatedCoach);

    void deleteCoach(Long id);
}
