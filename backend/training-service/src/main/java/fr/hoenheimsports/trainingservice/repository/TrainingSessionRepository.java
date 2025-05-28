package fr.hoenheimsports.trainingservice.repository;

import fr.hoenheimsports.trainingservice.model.TrainingSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainingSessionRepository extends JpaRepository<TrainingSession, Long> {
}