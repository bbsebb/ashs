package fr.hoenheimsports.trainingservice.repository;

import fr.hoenheimsports.trainingservice.model.Hall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

/**
 * Repository interface for managing Hall entities in the database.
 * 
 * <p>This repository provides CRUD operations for Hall entities,
 * allowing the application to store, retrieve, update, and delete sports halls.
 * It also provides a method to check if a hall with specific details already exists.</p>
 * 
 * @since 1.0
 */
public interface HallRepository extends JpaRepository<Hall, Long> {
    /**
     * Checks if a hall with the given name and address already exists in the database.
     * 
     * <p>This method is used to enforce uniqueness constraints when creating or updating halls.</p>
     * 
     * @param name The name of the hall, must not be null
     * @param street The street address of the hall, must not be null
     * @param city The city where the hall is located, must not be null
     * @param postalCode The postal code of the hall's location, must not be null
     * @param country The country where the hall is located, must not be null
     * @return true if a hall with the given name and address exists, false otherwise
     */
    boolean existsByNameAndAddress_StreetAndAddress_CityAndAddress_PostalCodeAndAddress_Country(@NonNull String name, @NonNull String street, @NonNull String city, @NonNull String postalCode, @NonNull String country);
}
