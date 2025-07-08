package fr.hoenheimsports.trainingservice.repository;

import fr.hoenheimsports.trainingservice.model.TrainingSession;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing TrainingSession entities in the database.
 * 
 * <p>This repository provides CRUD operations for TrainingSession entities,
 * allowing the application to store, retrieve, update, and delete training sessions.
 * Training sessions represent scheduled practice times for teams at specific halls.</p>
 * 
 * @since 1.0
 */
public interface TrainingSessionRepository extends JpaRepository<TrainingSession, Long> {
}
