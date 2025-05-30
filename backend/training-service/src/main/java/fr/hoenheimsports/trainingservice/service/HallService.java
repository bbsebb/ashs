package fr.hoenheimsports.trainingservice.service;

import fr.hoenheimsports.trainingservice.model.Hall;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface HallService {
    Hall createHall(Hall hall);

    Hall getHallById(Long id);

    Page<Hall> getHalls(Pageable pageable);

    List<Hall> getAllHalls();

    Hall updateHall(Long id, Hall updatedHall);

    void deleteHall(Long id);


    boolean isNotUniqueHall(String name, String street, String city, String postalCode, String country);
}
