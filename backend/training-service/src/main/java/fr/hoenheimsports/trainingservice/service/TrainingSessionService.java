package fr.hoenheimsports.trainingservice.service;

import fr.hoenheimsports.trainingservice.model.TrainingSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TrainingSessionService {
    TrainingSession createTrainingSession(TrainingSession trainingSession);

    TrainingSession getTrainingSessionById(Long id);

    Page<TrainingSession> getTrainingSessions(Pageable pageable);

    List<TrainingSession> getAllTrainingSessions();

    TrainingSession updateTrainingSession(Long id, TrainingSession updatedTrainingSession);

    void deleteTrainingSession(Long id);
}
