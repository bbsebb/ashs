package fr.hoenheimsports.trainingservice.service;

import fr.hoenheimsports.trainingservice.model.Coach;
import fr.hoenheimsports.trainingservice.repository.CoachRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CoachServiceImplTest {

    @Mock
    private CoachRepository coachRepository;

    @InjectMocks
    private CoachServiceImpl coachService;

    @Test
    void createCoach_shouldReturnCoach_whenValidRequest() {
        // Arrange
        Coach coach = getCoach();
        when(coachRepository.save(coach)).thenReturn(coach);

        // Act
        Coach result = coachService.createCoach(coach);

        // Assert
        assertThat(result).isEqualTo(coach);
        verify(coachRepository, times(1)).save(coach);
    }

    @Test
    void getCoachById_shouldReturnCoach_whenIdExists() {
        // Arrange
        Long coachId = 1L;
        Coach coach = getCoach(coachId);
        when(coachRepository.findById(coachId)).thenReturn(Optional.of(coach));

        // Act
        Coach result = coachService.getCoachById(coachId);

        // Assert
        assertThat(result).isEqualTo(coach);
        verify(coachRepository, times(1)).findById(coachId);
    }

    @Test
    void getCoachById_shouldThrowEntityNotFoundException_whenIdDoesNotExist() {
        // Arrange
        Long coachId = 1L;
        when(coachRepository.findById(coachId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> coachService.getCoachById(coachId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Coach introuvable avec l'id : " + coachId);

        verify(coachRepository, times(1)).findById(coachId);
    }

    @Test
    void getCoaches_shouldReturnPageOfCoaches_whenValidRequest() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Coach coach = getCoach();
        Page<Coach> coachPage = new PageImpl<>(List.of(coach));
        when(coachRepository.findAll(pageable)).thenReturn(coachPage);

        // Act
        Page<Coach> result = coachService.getCoaches(pageable);

        // Assert
        assertThat(result).isEqualTo(coachPage);
        verify(coachRepository, times(1)).findAll(pageable);
    }

    @Test
    void updateCoach_shouldReturnUpdatedCoach_whenValidIdAndRequest() {
        // Arrange
        Long coachId = 1L;
        Coach existingCoach = getCoach(coachId);
        Coach updatedCoach = Coach.builder()
                .id(coachId)
                .name("Updated Name")
                .surname("Updated Surname")
                .email("updated.email@example.com")
                .phone("+33123456789")
                .build();

        when(coachRepository.findById(coachId)).thenReturn(Optional.of(existingCoach));
        when(coachRepository.save(any(Coach.class))).thenReturn(updatedCoach);

        // Act
        Coach result = coachService.updateCoach(coachId, updatedCoach);

        // Assert
        assertThat(result).isEqualTo(updatedCoach);
        verify(coachRepository, times(1)).findById(coachId);
        verify(coachRepository, times(1)).save(any(Coach.class));
    }

    @Test
    void updateCoach_shouldThrowEntityNotFoundException_whenIdDoesNotExist() {
        // Arrange
        Long coachId = 1L;
        Coach updatedCoach = getCoach(coachId);
        when(coachRepository.findById(coachId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> coachService.updateCoach(coachId, updatedCoach))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Coach introuvable avec l'id : " + coachId);

        verify(coachRepository, times(1)).findById(coachId);
        verify(coachRepository, never()).save(any(Coach.class));
    }

    @Test
    void deleteCoach_shouldDeleteCoach_whenIdExists() {
        // Arrange
        Long coachId = 1L;
        when(coachRepository.existsById(coachId)).thenReturn(true);

        // Act
        coachService.deleteCoach(coachId);

        // Assert
        verify(coachRepository, times(1)).existsById(coachId);
        verify(coachRepository, times(1)).deleteById(coachId);
    }

    @Test
    void deleteCoach_shouldThrowEntityNotFoundException_whenIdDoesNotExist() {
        // Arrange
        Long coachId = 1L;
        when(coachRepository.existsById(coachId)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> coachService.deleteCoach(coachId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Coach introuvable avec l'id : " + coachId);

        verify(coachRepository, times(1)).existsById(coachId);
        verify(coachRepository, never()).deleteById(coachId);
    }

    // Méthodes utilitaires pour créer des entités de test
    private static Coach getCoach(Long coachId) {
        return Coach.builder()
                .id(coachId)
                .name("John")
                .surname("Doe")
                .email("john.doe@example.com")
                .phone("+33123456789")
                .build();
    }

    private static Coach getCoach() {
        return getCoach(1L);
    }
}