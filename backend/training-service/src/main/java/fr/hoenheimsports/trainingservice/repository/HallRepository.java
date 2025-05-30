package fr.hoenheimsports.trainingservice.repository;

import fr.hoenheimsports.trainingservice.model.Hall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

public interface HallRepository extends JpaRepository<Hall, Long> {
    boolean existsByNameAndAddress_StreetAndAddress_CityAndAddress_PostalCodeAndAddress_Country(@NonNull String name, @NonNull String street, @NonNull String city, @NonNull String postalCode, @NonNull String country);
}