package fr.hoenheimsports.trainingservice.service;

import fr.hoenheimsports.trainingservice.model.Coach;
import fr.hoenheimsports.trainingservice.repository.CoachRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * <p><b>CoachServiceImpl</b> implements the {@link CoachService} interface and provides business logic
 * for managing {@link Coach} entities.</p>
 *
 * <p><b>Main responsibilities:</b></p>
 * <ul>
 *     <li>Create and save a new coach (Coach).</li>
 *     <li>Retrieve a coach by their unique identifier.</li>
 *     <li>Get a paginated list of all coaches.</li>
 *     <li>Update the information of an existing coach.</li>
 *     <li>Delete a coach by their unique identifier.</li>
 * </ul>
 *
 * <p>This class uses the {@link CoachRepository} to perform persistence operations
 * and throws a {@link EntityNotFoundException} when no matching entity is found.</p>
 */
@Service
@Slf4j
public class CoachServiceImpl implements CoachService {

    private final CoachRepository coachRepository;

    /**
     * Constructor to inject the {@link CoachRepository}.
     *
     * @param coachRepository the repository for coach operations
     */
    public CoachServiceImpl(CoachRepository coachRepository) {
        this.coachRepository = coachRepository;
    }

    /**
     * Creates a new coach and saves it in the repository.
     *
     * @param coach the Coach entity to create and save
     * @return the saved Coach entity
     */
    @Override
    public Coach createCoach(Coach coach) {
        log.info("Création d'un nouveau coach: {} {}", coach.getName(), coach.getSurname());
        log.debug("Détails du coach: email={}, téléphone={}", coach.getEmail(), coach.getPhone());
        Coach savedCoach = coachRepository.save(coach);
        log.info("Coach créé avec succès, ID: {}", savedCoach.getId());
        return savedCoach;
    }

    /**
     * Retrieves a coach by their unique identifier.
     *
     * @param id the unique identifier of the Coach to retrieve
     * @return the corresponding Coach entity
     * @throws EntityNotFoundException if no coach with the given identifier is found
     */
    @Override
    public Coach getCoachById(Long id) {
        log.debug("Recherche du coach avec l'ID: {}", id);
        Coach coach = coachRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Coach introuvable avec l'ID: {}", id);
                    return new EntityNotFoundException("Coach introuvable avec l'id : " + id);
                });
        log.debug("Coach trouvé: {} {}", coach.getName(), coach.getSurname());
        return coach;
    }

    /**
     * Retrieves a paginated list of coaches from the repository.
     *
     * @param pageable the pagination details, including page number, size, and sorting options
     * @return a page of Coach entities based on the provided pagination details
     */
    @Override
    public Page<Coach> getCoaches(Pageable pageable) {
        log.debug("Récupération des coachs paginés: page={}, taille={}, tri={}", 
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        Page<Coach> coaches = coachRepository.findAll(pageable);
        log.debug("Nombre de coachs récupérés: {}", coaches.getNumberOfElements());
        return coaches;
    }

    /**
     * Retrieves a list of all coaches from the repository.
     *
     * @return a list containing all Coach entities in the repository
     */
    @Override
    public List<Coach> getAllCoaches() {
        log.debug("Récupération de tous les coachs");
        List<Coach> coaches = coachRepository.findAll();
        log.debug("Nombre total de coachs récupérés: {}", coaches.size());
        return coaches;
    }


    /**
     * Updates the information of an existing coach.
     *
     * @param id           the unique identifier of the Coach to update
     * @param updatedCoach the Coach entity containing the updated information
     * @return the updated Coach entity after saving it in the repository
     * @throws EntityNotFoundException if no coach with the given identifier is found
     */
    @Override
    @Transactional
    public Coach updateCoach(Long id, Coach updatedCoach) {
        log.info("Mise à jour du coach avec l'ID: {}", id);
        log.debug("Nouvelles informations: nom={}, prénom={}, email={}, téléphone={}", 
                updatedCoach.getName(), updatedCoach.getSurname(), 
                updatedCoach.getEmail(), updatedCoach.getPhone());

        Coach coach = coachRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Tentative de mise à jour d'un coach inexistant, ID: {}", id);
                    return new EntityNotFoundException("Coach introuvable avec l'id : " + id);
                });

        log.debug("Coach trouvé pour mise à jour: {} {}", coach.getName(), coach.getSurname());
        coach.setName(updatedCoach.getName());
        coach.setSurname(updatedCoach.getSurname());
        coach.setEmail(updatedCoach.getEmail());
        coach.setPhone(updatedCoach.getPhone());

        Coach savedCoach = coachRepository.save(coach);
        log.info("Coach mis à jour avec succès, ID: {}", savedCoach.getId());
        return savedCoach;
    }

    /**
     * Deletes a coach by their unique identifier.
     *
     * @param id the unique identifier of the Coach to delete
     * @throws EntityNotFoundException if no coach with the given identifier is found
     */
    @Override
    public void deleteCoach(Long id) {
        log.info("Suppression du coach avec l'ID: {}", id);
        if (!coachRepository.existsById(id)) {
            log.warn("Tentative de suppression d'un coach inexistant, ID: {}", id);
            throw new EntityNotFoundException("Coach introuvable avec l'id : " + id);
        }
        coachRepository.deleteById(id);
        log.info("Coach supprimé avec succès, ID: {}", id);
    }
}
