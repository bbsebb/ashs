package fr.hoenheimsports.trainingservice.service;

import fr.hoenheimsports.trainingservice.exception.HallAlreadyExistsException;
import fr.hoenheimsports.trainingservice.model.Hall;
import fr.hoenheimsports.trainingservice.repository.HallRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;


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
@Slf4j
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
        log.info("Création d'une nouvelle salle: {}", hall.getName());
        log.debug("Détails de la salle: adresse={}, {}, {}, {}", 
                hall.getAddress().getStreet(), hall.getAddress().getPostalCode(), 
                hall.getAddress().getCity(), hall.getAddress().getCountry());

        if (isNotUniqueHall(hall)) {
            log.warn("Tentative de création d'une salle déjà existante: {}", hall.getName());
            var messageError = """
                    Hall already exists with combinaison of
                     name : %s
                     street : %s
                     cp : %s
                     city: %s
                     country: %s
                    """.formatted(hall.getName(), hall.getAddress().getStreet(), hall.getAddress().getPostalCode(), hall.getAddress().getCity(), hall.getAddress().getCountry());
            throw new HallAlreadyExistsException(messageError);
        }

        Hall savedHall = hallRepository.save(hall);
        log.info("Salle créée avec succès, ID: {}", savedHall.getId());
        return savedHall;
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
        log.debug("Recherche de la salle avec l'ID: {}", id);
        Hall hall = hallRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Salle introuvable avec l'ID: {}", id);
                    return new EntityNotFoundException("Hall not found with id: " + id);
                });
        log.debug("Salle trouvée: {}, adresse: {}, {}, {}", 
                hall.getName(), hall.getAddress().getCity(), 
                hall.getAddress().getPostalCode(), hall.getAddress().getCountry());
        return hall;
    }


    /**
     * Retrieves a paginated list of Hall entities from the repository.
     *
     * @param pageable the pagination information, including page number, size, and sorting options
     * @return a page of Hall entities based on the provided pagination information
     */
    @Override
    public Page<Hall> getHalls(Pageable pageable) {
        log.debug("Récupération des salles paginées: page={}, taille={}, tri={}", 
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        Page<Hall> halls = hallRepository.findAll(pageable);
        log.debug("Nombre de salles récupérées: {}", halls.getNumberOfElements());
        return halls;
    }

    /**
     * Retrieves a list of all Hall entities from the repository.
     *
     * @return a list containing all Hall entities in the repository
     */
    @Override
    public List<Hall> getAllHalls() {
        log.debug("Récupération de toutes les salles");
        List<Hall> halls = hallRepository.findAll();
        log.debug("Nombre total de salles récupérées: {}", halls.size());
        return halls;
    }


    /**
     * Updates an existing Hall entity with new information.
     *
     * @param id          the unique identifier of the Hall to update
     * @param updatedHall the Hall entity containing updated information
     * @return the updated Hall entity after saving to the repository
     * @throws EntityNotFoundException if no Hall entity with the given identifier is found
     */
    @Override
    @Transactional
    public Hall updateHall(Long id, Hall updatedHall) {
        log.info("Mise à jour de la salle avec l'ID: {}", id);
        log.debug("Nouvelles informations: nom={}, adresse={}, {}, {}, {}", 
                updatedHall.getName(), updatedHall.getAddress().getStreet(), 
                updatedHall.getAddress().getPostalCode(), updatedHall.getAddress().getCity(), 
                updatedHall.getAddress().getCountry());

        Hall hall = hallRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Tentative de mise à jour d'une salle inexistante, ID: {}", id);
                    return new EntityNotFoundException("Hall not found with id: " + id);
                });

        log.debug("Salle trouvée pour mise à jour: {}", hall.getName());

        if (areEqual(hall, updatedHall)) {
            log.debug("Aucune modification nécessaire, les données sont identiques");
            return hall;
        }

        if (isNotUniqueHall(updatedHall)) {
            log.warn("Tentative de mise à jour vers une salle déjà existante: {}", updatedHall.getName());
            var messageError = """
                    Hall already exists with combinaison of
                     name : %s
                     street : %s
                     cp : %s
                     city: %s
                     country: %s
                    """.formatted(updatedHall.getName(), updatedHall.getAddress().getStreet(), updatedHall.getAddress().getPostalCode(), updatedHall.getAddress().getCity(), updatedHall.getAddress().getCountry());
            throw new HallAlreadyExistsException(messageError);
        }

        hall.setName(updatedHall.getName());
        hall.setAddress(updatedHall.getAddress());

        Hall savedHall = hallRepository.save(hall);
        log.info("Salle mise à jour avec succès, ID: {}", savedHall.getId());
        return savedHall;
    }

    private static boolean areEqual(Hall hall1, Hall hall2) {
        if (hall1 == hall2) return true;
        if (hall1 == null || hall2 == null) return false;

        return Objects.equals(hall1.getName(), hall2.getName()) &&
                Objects.equals(hall1.getAddress(), hall2.getAddress());
    }


    /**
     * Deletes a Hall entity by its unique identifier.
     *
     * @param id the unique identifier of the Hall to delete
     * @throws EntityNotFoundException if no Hall entity with the given identifier is found
     */
    @Override
    public void deleteHall(Long id) {
        log.info("Suppression de la salle avec l'ID: {}", id);
        if (!hallRepository.existsById(id)) {
            log.warn("Tentative de suppression d'une salle inexistante, ID: {}", id);
            throw new EntityNotFoundException("Hall not found with id: " + id);
        }
        hallRepository.deleteById(id);
        log.info("Salle supprimée avec succès, ID: {}", id);
    }


    private boolean isNotUniqueHall(Hall hall) {
        log.debug("Vérification de l'unicité de la salle: {}", hall.getName());
        return isNotUniqueHall(
                hall.getName(), hall.getAddress().getStreet(), hall.getAddress().getCity(), hall.getAddress().getPostalCode(), hall.getAddress().getCountry()
        );
    }

    @Override
    public boolean isNotUniqueHall(String name, String street, String city, String postalCode, String country) {
        log.debug("Vérification de l'unicité de la salle avec les paramètres: nom={}, adresse={}, {}, {}, {}", 
                name, street, postalCode, city, country);
        boolean exists = hallRepository.existsByNameAndAddress_StreetAndAddress_CityAndAddress_PostalCodeAndAddress_Country(
                name, street, city, postalCode, country
        );
        log.debug("Résultat de la vérification d'unicité: salle {} existante", exists ? "déjà" : "non");
        return exists;
    }

}
