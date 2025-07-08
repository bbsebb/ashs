package fr.hoenheimsports.trainingservice.service;

import fr.hoenheimsports.trainingservice.model.Hall;
import fr.hoenheimsports.trainingservice.model.Team;
import fr.hoenheimsports.trainingservice.model.TrainingSession;
import fr.hoenheimsports.trainingservice.repository.TrainingSessionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p><b>TrainingSessionServiceImpl</b> implémente l'interface {@link TrainingSessionService}
 * et fournit la logique métier pour la gestion des entités {@link TrainingSession}.</p>
 *
 * <p><b>Responsabilités principales :</b></p>
 * <ul>
 *     <li>Créer et enregistrer une nouvelle session d'entraînement.</li>
 *     <li>Récupérer une session d'entraînement par son identifiant unique.</li>
 *     <li>Obtenir une liste paginée de toutes les sessions d'entraînement.</li>
 *     <li>Mettre à jour les informations d'une session d'entraînement existante.</li>
 *     <li>Supprimer une session d'entraînement par son identifiant unique.</li>
 * </ul>
 */
@Service
@Slf4j
public class TrainingSessionServiceImpl implements TrainingSessionService {

    private final TrainingSessionRepository trainingSessionRepository;

    /**
     * Constructeur pour injecter le {@link TrainingSessionRepository}.
     *
     * @param trainingSessionRepository le référentiel pour effectuer les opérations sur les sessions d'entraînement
     */
    public TrainingSessionServiceImpl(TrainingSessionRepository trainingSessionRepository) {
        this.trainingSessionRepository = trainingSessionRepository;
    }

    /**
     * Crée et enregistre une nouvelle session d'entraînement.
     *
     * @param trainingSession l'entité de session d'entraînement à enregistrer
     * @return la session d'entraînement enregistrée
     */
    @Override
    public TrainingSession createTrainingSession(TrainingSession trainingSession) {
        log.info("Création d'une nouvelle séance d'entraînement");

        if (trainingSession.getTimeSlot() != null) {
            log.debug("Détails de la séance: jour={}, heure de début={}, heure de fin={}", 
                    trainingSession.getTimeSlot().getDayOfWeek(), 
                    trainingSession.getTimeSlot().getStartTime(), 
                    trainingSession.getTimeSlot().getEndTime());
        }

        if (trainingSession.getTimeSlot() == null || !trainingSession.getTimeSlot().isValid()) {
            log.warn("Tentative de création d'une séance avec un créneau horaire invalide");
            throw new IllegalStateException("TimeSlot doesn't valid");
        }

        TrainingSession savedSession = trainingSessionRepository.save(trainingSession);
        log.info("Séance d'entraînement créée avec succès, ID: {}", savedSession.getId());
        return savedSession;
    }

    /**
     * Récupère une session d'entraînement par son identifiant unique.
     *
     * @param id l'identifiant unique de la session d'entraînement
     * @return la session d'entraînement correspondante à cet identifiant
     * @throws EntityNotFoundException si aucune session d'entraînement n'est trouvée avec cet identifiant
     */
    @Override
    public TrainingSession getTrainingSessionById(Long id) {
        log.debug("Recherche de la séance d'entraînement avec l'ID: {}", id);
        TrainingSession session = trainingSessionRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Séance d'entraînement introuvable avec l'ID: {}", id);
                    return new EntityNotFoundException("TrainingSession introuvable avec l'id : " + id);
                });

        if (session.getTimeSlot() != null) {
            log.debug("Séance trouvée: jour={}, heure de début={}, heure de fin={}", 
                    session.getTimeSlot().getDayOfWeek(), 
                    session.getTimeSlot().getStartTime(), 
                    session.getTimeSlot().getEndTime());
        }

        return session;
    }

    /**
     * Récupère une liste paginée de toutes les sessions d'entraînement.
     *
     * @param pageable les informations de pagination (page, taille, tri, etc.)
     * @return une page contenant les sessions d'entraînement
     */
    @Override
    public Page<TrainingSession> getTrainingSessions(Pageable pageable) {
        log.debug("Récupération des séances d'entraînement paginées: page={}, taille={}, tri={}", 
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        Page<TrainingSession> sessions = trainingSessionRepository.findAll(pageable);
        log.debug("Nombre de séances récupérées: {}", sessions.getNumberOfElements());
        return sessions;
    }

    /**
     * Récupère une liste de toutes les sessions d'entraînement.
     *
     * @return une liste contenant toutes les sessions d'entraînement
     */
    @Override
    public List<TrainingSession> getAllTrainingSessions() {
        log.debug("Récupération de toutes les séances d'entraînement");
        List<TrainingSession> sessions = trainingSessionRepository.findAll();
        log.debug("Nombre total de séances récupérées: {}", sessions.size());
        return sessions;
    }

    /**
     * Met à jour les informations d'une session d'entraînement existante.
     *
     * @param id                     l'identifiant unique de la session à mettre à jour
     * @param updatedTrainingSession l'entité contenant les informations mises à jour
     * @return l'entité mise à jour après l'avoir enregistrée dans le référentiel
     * @throws EntityNotFoundException si aucune session d'entraînement n'est trouvée avec cet identifiant
     */
    @Override
    @Transactional
    public TrainingSession updateTrainingSession(Long id, TrainingSession updatedTrainingSession) {
        log.info("Mise à jour de la séance d'entraînement avec l'ID: {}", id);

        if (updatedTrainingSession.getTimeSlot() != null) {
            log.debug("Nouvelles informations: jour={}, heure de début={}, heure de fin={}", 
                    updatedTrainingSession.getTimeSlot().getDayOfWeek(), 
                    updatedTrainingSession.getTimeSlot().getStartTime(), 
                    updatedTrainingSession.getTimeSlot().getEndTime());
        }

        TrainingSession trainingSession = trainingSessionRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Tentative de mise à jour d'une séance inexistante, ID: {}", id);
                    return new EntityNotFoundException("TrainingSession introuvable avec l'id : " + id);
                });

        log.debug("Séance trouvée pour mise à jour, ID: {}", trainingSession.getId());

        if (updatedTrainingSession.getTimeSlot() != null && updatedTrainingSession.getTimeSlot().isValid()) {
            log.debug("Mise à jour du créneau horaire");
            trainingSession.setTimeSlot(updatedTrainingSession.getTimeSlot());
        } else {
            log.warn("Tentative de mise à jour avec un créneau horaire invalide");
            throw new IllegalStateException("TimeSlot doesn't valid");
        }

        TrainingSession savedSession = trainingSessionRepository.save(trainingSession);
        log.info("Séance d'entraînement mise à jour avec succès, ID: {}", savedSession.getId());
        return savedSession;
    }

    /**
     * Supprime une session d'entraînement par son identifiant unique.
     *
     * @param id l'identifiant unique de la session à supprimer
     * @throws EntityNotFoundException si aucune session d'entraînement n'est trouvée avec cet identifiant
     */
    @Override
    @Transactional
    public void deleteTrainingSession(Long id) {
        log.info("Suppression de la séance d'entraînement avec l'ID: {}", id);

        TrainingSession trainingSession = this.getTrainingSessionById(id);
        Team team = trainingSession.getTeam();
        Hall hall = trainingSession.getHall();

        log.debug("Suppression de l'association avec l'équipe: {}/{}/{}", 
                team.getGender(), team.getCategory(), team.getTeamNumber());
        team.removeTrainingSession(trainingSession);

        log.debug("Suppression de l'association avec la salle: {}", hall.getName());
        hall.removeTrainingSession(trainingSession);

        log.info("Séance d'entraînement supprimée avec succès, ID: {}", id);
    }


}
