package fr.hoenheimsports.facebookservice.repository;

import fr.hoenheimsports.facebookservice.model.AccessToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing AccessToken entities in the database.
 * 
 * <p>This repository provides CRUD operations for AccessToken entities.
 * The application stores only one token at a time, which is identified by ID 1.</p>
 * 
 * @since 1.0
 */
@Repository
public interface AccessTokenRepository extends JpaRepository<AccessToken, Long> {
}
