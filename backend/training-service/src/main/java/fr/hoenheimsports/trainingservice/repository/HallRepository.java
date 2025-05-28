package fr.hoenheimsports.trainingservice.repository;

import fr.hoenheimsports.trainingservice.model.Hall;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HallRepository extends JpaRepository<Hall, Long> {
}