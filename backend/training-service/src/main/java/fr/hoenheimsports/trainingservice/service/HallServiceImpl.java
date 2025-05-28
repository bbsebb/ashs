package fr.hoenheimsports.trainingservice.service;

import fr.hoenheimsports.trainingservice.model.Hall;
import fr.hoenheimsports.trainingservice.repository.HallRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * <p><b>HallServiceImpl</b> implements the {@link HallService} interface and provides business logic
 * for managing {@link Hall} entities.</p>
 *
 * <p><b>Main Responsibilities:</b></p>
 * <ul>
 *     <li>Create and save a new Hall entity.</li>
 *     <li>Retrieve a Hall by its unique identifier.</li>
 *     <li>Fetch a paginated list of all Halls.</li>
 *     <li>Update an existing Hall's information.</li>
 *     <li>Delete a Hall by its unique identifier.</li>
 * </ul>
 *
 * <p>The class performs persistence operations using the {@link HallRepository}, and throws
 * {@link EntityNotFoundException} when an operation involves a non-existing entity.</p>
 */
@Service
public class HallServiceImpl implements HallService {

    private final HallRepository hallRepository;


    public HallServiceImpl(HallRepository hallRepository) {
        this.hallRepository = hallRepository;
    }


    /**
     * Creates a new Hall entity and saves it to the repository.
     *
     * @param hall the Hall entity to be created and saved
     * @return the saved Hall entity
     */
    @Override
    public Hall createHall(Hall hall) {
        return hallRepository.save(hall);
    }


    /**
     * Retrieves a Hall entity by its unique identifier.
     *
     * @param id the unique identifier of the Hall to retrieve
     * @return the Hall entity with the specified identifier
     * @throws EntityNotFoundException if no Hall entity with the given identifier is found
     */
    @Override
    public Hall getHallById(Long id) {
        return  hallRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Hall not found with id: " + id));
    }


    /**
     * Retrieves a paginated list of Hall entities from the repository.
     *
     * @param pageable the pagination information, including page number, size, and sorting options
     * @return a page of Hall entities based on the provided pagination information
     */
    @Override
    public Page<Hall> getHalls(Pageable pageable) {
        return hallRepository.findAll(pageable);
    }

    /**
     * Retrieves a list of all Hall entities from the repository.
     *
     * @return a list containing all Hall entities in the repository
     */
    @Override
    public List<Hall> getAllHalls() {
        return hallRepository.findAll();
    }


    /**
     * Updates an existing Hall entity with new information.
     *
     * @param id the unique identifier of the Hall to update
     * @param updatedHall the Hall entity containing updated information
     * @return the updated Hall entity after saving to the repository
     * @throws EntityNotFoundException if no Hall entity with the given identifier is found
     */
    @Override
    @Transactional
    public Hall updateHall(Long id, Hall updatedHall) {
        Hall hall = hallRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Hall not found with id: " + id));
        hall.setName(updatedHall.getName());
        hall.setAddress(updatedHall.getAddress());
        return hallRepository.save(hall);
    }


    /**
     * Deletes a Hall entity by its unique identifier.
     *
     * @param id the unique identifier of the Hall to delete
     * @throws EntityNotFoundException if no Hall entity with the given identifier is found
     */
    @Override
    public void deleteHall(Long id) {
        if (!hallRepository.existsById(id)) {
            throw new EntityNotFoundException("Hall not found with id: " + id);
        }
        hallRepository.deleteById(id);
    }


}
