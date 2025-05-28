package fr.hoenheimsports.trainingservice.service;

import fr.hoenheimsports.trainingservice.model.Hall;
import fr.hoenheimsports.trainingservice.model.Team;
import fr.hoenheimsports.trainingservice.model.TrainingSession;
import fr.hoenheimsports.trainingservice.repository.TrainingSessionRepository;
import jakarta.persistence.EntityNotFoundException;
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
        if (trainingSession.getTimeSlot() == null || !trainingSession.getTimeSlot().isValid()) {
            throw new IllegalStateException("TimeSlot doesn't valid");
        }
        return trainingSessionRepository.save(trainingSession);
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
        return trainingSessionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("TrainingSession introuvable avec l'id : " + id));
    }

    /**
     * Récupère une liste paginée de toutes les sessions d'entraînement.
     *
     * @param pageable les informations de pagination (page, taille, tri, etc.)
     * @return une page contenant les sessions d'entraînement
     */
    @Override
    public Page<TrainingSession> getTrainingSessions(Pageable pageable) {
        return trainingSessionRepository.findAll(pageable);
    }

    /**
     * Récupère une liste de toutes les sessions d'entraînement.
     *
     * @return une liste contenant toutes les sessions d'entraînement
     */
    @Override
    public List<TrainingSession> getAllTrainingSessions() {
        return trainingSessionRepository.findAll();
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
        TrainingSession trainingSession = trainingSessionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("TrainingSession introuvable avec l'id : " + id));
        if (updatedTrainingSession.getTimeSlot() != null && updatedTrainingSession.getTimeSlot().isValid()) {
            trainingSession.setTimeSlot(updatedTrainingSession.getTimeSlot());
        } else {
            throw new IllegalStateException("TimeSlot doesn't valid");
        }

        return trainingSessionRepository.save(trainingSession);
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
        TrainingSession trainingSession = this.getTrainingSessionById(id);
        Team team = trainingSession.getTeam();
        Hall hall = trainingSession.getHall();
        team.removeTrainingSession(trainingSession);
        hall.removeTrainingSession(trainingSession);
    }


}
