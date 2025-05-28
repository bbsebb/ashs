package fr.hoenheimsports.trainingservice.repository;

import fr.hoenheimsports.trainingservice.model.Coach;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CoachRepository extends JpaRepository<Coach, Long> {

}