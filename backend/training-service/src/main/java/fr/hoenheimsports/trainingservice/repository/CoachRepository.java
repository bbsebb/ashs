package fr.hoenheimsports.trainingservice.repository;

import fr.hoenheimsports.trainingservice.model.Coach;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * Repository interface for managing Coach entities in the database.
 * 
 * <p>This repository provides CRUD operations for Coach entities,
 * allowing the application to store, retrieve, update, and delete coaches.</p>
 * 
 * @since 1.0
 */
public interface CoachRepository extends JpaRepository<Coach, Long> {

}
