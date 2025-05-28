package fr.hoenheimsports.trainingservice.service;

import fr.hoenheimsports.trainingservice.model.TimeSlot;
import fr.hoenheimsports.trainingservice.model.TrainingSession;
import fr.hoenheimsports.trainingservice.repository.TrainingSessionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TrainingSessionServiceImplTest {

    @Mock
    private TrainingSessionRepository trainingSessionRepository;

    @InjectMocks
    private TrainingSessionServiceImpl trainingSessionService;

    @Test
    void createTrainingSession_shouldReturnTrainingSession_whenValidRequest() {
        TrainingSession trainingSession = getTrainingSession();
        when(trainingSessionRepository.save(trainingSession)).thenReturn(trainingSession);

        TrainingSession result = trainingSessionService.createTrainingSession(trainingSession);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(trainingSession.getId());
        verify(trainingSessionRepository, times(1)).save(trainingSession);
    }

    @Test
    void createTrainingSession_shouldReturnTrainingSession_whenTimeSlotDoesntValid() {
        TrainingSession trainingSession = getTrainingSession();
        trainingSession.getTimeSlot().setStartTime(trainingSession.getTimeSlot().getEndTime().plusHours(1));

        assertThatThrownBy(() -> trainingSessionService.createTrainingSession(trainingSession))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("TimeSlot doesn't valid");

    }

    @Test
    void createTrainingSession_shouldReturnTrainingSession_whenTimeSlotIsNull() {
        TrainingSession trainingSession = getTrainingSession();
        trainingSession.setTimeSlot(null);

        assertThatThrownBy(() -> trainingSessionService.createTrainingSession(trainingSession))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("TimeSlot doesn't valid");

    }

    @Test
    void getTrainingSessionById_shouldReturnTrainingSession_whenIdExists() {
        Long id = 1L;
        TrainingSession trainingSession = getTrainingSession();
        when(trainingSessionRepository.findById(id)).thenReturn(Optional.of(trainingSession));

        TrainingSession result = trainingSessionService.getTrainingSessionById(id);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        verify(trainingSessionRepository, times(1)).findById(id);
    }

    @Test
    void getTrainingSessionById_shouldThrowEntityNotFoundException_whenIdDoesNotExist() {
        Long id = 1L;
        when(trainingSessionRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainingSessionService.getTrainingSessionById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("TrainingSession introuvable avec l'id : " + id);

        verify(trainingSessionRepository, times(1)).findById(id);
    }

    @Test
    void getTrainingSessions_shouldReturnPageOfTrainingSessions_whenValidRequest() {
        Pageable pageable = PageRequest.of(0, 10);
        List<TrainingSession> trainingSessionList = List.of(getTrainingSession(), getTrainingSession(2L));
        Page<TrainingSession> page = new PageImpl<>(trainingSessionList);

        when(trainingSessionRepository.findAll(pageable)).thenReturn(page);

        Page<TrainingSession> result = trainingSessionService.getTrainingSessions(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent().size()).isEqualTo(trainingSessionList.size());
        verify(trainingSessionRepository, times(1)).findAll(pageable);
    }

    @Test
    void updateTrainingSession_shouldReturnUpdatedTrainingSession_whenValidIdAndRequest() {
        Long id = 1L;
        TrainingSession existingSession = Mockito.spy(getTrainingSession());
        TrainingSession updatedSession = getTrainingSession();
        updatedSession.setTimeSlot(new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(10,0),LocalTime.of(13,0))); // Exemple de mise à jour

        when(trainingSessionRepository.findById(id)).thenReturn(Optional.of(existingSession));
        when(trainingSessionRepository.save(existingSession)).thenReturn(updatedSession);

        TrainingSession result = trainingSessionService.updateTrainingSession(id, updatedSession);


        assertThat(result).isNotNull();
        assertThat(result.getTeam()).isEqualTo(updatedSession.getTeam());
        verify(existingSession).setTimeSlot(updatedSession.getTimeSlot());
        verifyNoMoreInteractions(existingSession);
        verify(trainingSessionRepository, times(1)).findById(id);
        verify(trainingSessionRepository, times(1)).save(existingSession);
    }

    @Test
    void updateTrainingSession_shouldThrowEntityNotFoundException_whenIdDoesNotExist() {
        Long id = 1L;
        TrainingSession updatedSession = getTrainingSession();

        when(trainingSessionRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainingSessionService.updateTrainingSession(id, updatedSession))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("TrainingSession introuvable avec l'id : " + id);

        verify(trainingSessionRepository, times(1)).findById(id);
        verify(trainingSessionRepository, times(0)).save(any());
    }

    @Test
    void updateTrainingSession_shouldReturnTrainingSession_whenTimeSlotDoesntValid() {
        Long id = 1L;
        TrainingSession existingSession = getTrainingSession();
        TrainingSession updatedSession = getTrainingSession();
        updatedSession.setTimeSlot(new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(13,0),LocalTime.of(10,0))); // Exemple de mise à jour

        when(trainingSessionRepository.findById(id)).thenReturn(Optional.of(existingSession));


        assertThatThrownBy(() -> trainingSessionService.updateTrainingSession(id, updatedSession))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("TimeSlot doesn't valid");

        verify(trainingSessionRepository, times(1)).findById(id);
        verify(trainingSessionRepository, times(0)).save(any());
    }

    @Test
    void updateTrainingSession_shouldReturnTrainingSession_whenTimeSlotIsNull() {
        Long id = 1L;
        TrainingSession existingSession = getTrainingSession();
        TrainingSession updatedSession = getTrainingSession();
        updatedSession.setTimeSlot(null); // Exemple de mise à jour

        when(trainingSessionRepository.findById(id)).thenReturn(Optional.of(existingSession));


        assertThatThrownBy(() -> trainingSessionService.updateTrainingSession(id, updatedSession))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("TimeSlot doesn't valid");

        verify(trainingSessionRepository, times(1)).findById(id);
        verify(trainingSessionRepository, times(0)).save(any());
    }

    @Test
    void deleteTrainingSession_shouldDeleteTrainingSession_whenIdExists() {
        Long id = 1L;
        when(trainingSessionRepository.existsById(id)).thenReturn(true);

        trainingSessionService.deleteTrainingSession(id);

        verify(trainingSessionRepository, times(1)).existsById(id);
        verify(trainingSessionRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteTrainingSession_shouldThrowEntityNotFoundException_whenIdDoesNotExist() {
        Long id = 1L;

        when(trainingSessionRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> trainingSessionService.deleteTrainingSession(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("TrainingSession introuvable avec l'id : " + id);

        verify(trainingSessionRepository, times(1)).existsById(id);
        verify(trainingSessionRepository, times(0)).deleteById(id);
    }

    // Méthodes utilitaires pour créer des entités de test

    private static TrainingSession getTrainingSession(Long sessionId) {
        TrainingSession trainingSession = new TrainingSession();
        trainingSession.setId(sessionId);
        trainingSession.setTimeSlot(new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(10,0),LocalTime.of(11,0)) );
        return trainingSession;
    }

    private static TrainingSession getTrainingSession() {
        return getTrainingSession(1L);
    }
}