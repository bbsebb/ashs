package fr.hoenheimsports.trainingservice.service;

import fr.hoenheimsports.trainingservice.model.Coach;
import fr.hoenheimsports.trainingservice.repository.CoachRepository;
import jakarta.persistence.EntityNotFoundException;
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
        return coachRepository.save(coach);
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
        return coachRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Coach introuvable avec l'id : " + id));
    }

    /**
     * Retrieves a paginated list of coaches from the repository.
     *
     * @param pageable the pagination details, including page number, size, and sorting options
     * @return a page of Coach entities based on the provided pagination details
     */
    @Override
    public Page<Coach> getCoaches(Pageable pageable) {
        return coachRepository.findAll(pageable);
    }

    /**
     * Retrieves a list of all coaches from the repository.
     *
     * @return a list containing all Coach entities in the repository
     */
    @Override
    public List<Coach> getAllCoaches() {
        return coachRepository.findAll();
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
        Coach coach = coachRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Coach introuvable avec l'id : " + id));
        coach.setName(updatedCoach.getName());
        coach.setSurname(updatedCoach.getSurname());
        coach.setEmail(updatedCoach.getEmail());
        coach.setPhone(updatedCoach.getPhone());
        return coachRepository.save(coach);
    }

    /**
     * Deletes a coach by their unique identifier.
     *
     * @param id the unique identifier of the Coach to delete
     * @throws EntityNotFoundException if no coach with the given identifier is found
     */
    @Override
    public void deleteCoach(Long id) {
        if (!coachRepository.existsById(id)) {
            throw new EntityNotFoundException("Coach introuvable avec l'id : " + id);
        }
        coachRepository.deleteById(id);
    }
}
