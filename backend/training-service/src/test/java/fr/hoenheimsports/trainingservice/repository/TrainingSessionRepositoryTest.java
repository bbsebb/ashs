package fr.hoenheimsports.trainingservice.repository;

import fr.hoenheimsports.trainingservice.model.*;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.*;

@Testcontainers
@DataJpaTest
class TrainingSessionRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    TrainingSessionRepository trainingSessionRepository;

    @Autowired
    HallRepository hallRepository;

    @Autowired
    TeamRepository teamRepository;

    @Test
    void testValidTrainingSession() {
        // Arrange
        TrainingSession trainingSession = createValidTrainingSession();

        // Act
        TrainingSession savedTrainingSession = trainingSessionRepository.save(trainingSession);

        // Assert
        assertThat(savedTrainingSession.getId()).isNotNull();
        assertThat(savedTrainingSession.getTimeSlot()).isEqualTo(trainingSession.getTimeSlot());
        assertThat(savedTrainingSession.getHall()).isEqualTo(trainingSession.getHall());
        assertThat(savedTrainingSession.getTeam()).isEqualTo(trainingSession.getTeam());
    }

    @Test
    void testTimeSlotNotNullConstraint() {
        // Arrange
        TrainingSession trainingSession = createValidTrainingSession();
        trainingSession.setTimeSlot(null);

        // Act & Assert
        assertThatThrownBy(() -> trainingSessionRepository.save(trainingSession))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("timeSlot");
    }

    @Test
    void testHallNotNullConstraint() {
        // Arrange
        TrainingSession trainingSession = createValidTrainingSession();
        trainingSession.setHall(null);

        // Act & Assert
        assertThatThrownBy(() -> trainingSessionRepository.save(trainingSession))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("hall");
    }

    @Test
    void testTeamNotNullConstraint() {
        // Arrange
        TrainingSession trainingSession = createValidTrainingSession();
        trainingSession.setTeam(null);

        // Act & Assert
        assertThatThrownBy(() -> trainingSessionRepository.save(trainingSession))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("team");
    }

    @Test
    void testTimeSlotDayOfWeekNotNullConstraint() {
        // Arrange
        TrainingSession trainingSession = createValidTrainingSession();
        trainingSession.getTimeSlot().setDayOfWeek(null);

        // Act & Assert
        assertThatThrownBy(() -> trainingSessionRepository.saveAndFlush(trainingSession))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("dayOfWeek");
    }

    @Test
    void testTimeSlotStartTimeNotNullConstraint() {
        // Arrange
        TrainingSession trainingSession = createValidTrainingSession();
        trainingSession.getTimeSlot().setStartTime(null);

        // Act & Assert
        assertThatThrownBy(() -> trainingSessionRepository.save(trainingSession))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("startTime");
    }

    @Test
    void testTimeSlotEndTimeNotNullConstraint() {
        // Arrange
        TrainingSession trainingSession = createValidTrainingSession();
        trainingSession.getTimeSlot().setEndTime(null);

        // Act & Assert
        assertThatThrownBy(() -> trainingSessionRepository.save(trainingSession))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("endTime");
    }

    //@Test
    void testUpdateTrainingSession() {
        // Arrange
        TrainingSession trainingSession = createValidTrainingSession();
        TrainingSession savedTrainingSession = trainingSessionRepository.save(trainingSession);
        // Act


        // Assert
        assertThat(savedTrainingSession.getId()).isNotNull();
        assertThat(savedTrainingSession.getTimeSlot()).isEqualTo(trainingSession.getTimeSlot());
        assertThat(savedTrainingSession.getHall()).isEqualTo(trainingSession.getHall());
        assertThat(savedTrainingSession.getTeam()).isEqualTo(trainingSession.getTeam());

    }


    // Méthodes utilitaires pour créer des entités valides

    private TrainingSession createValidTrainingSession() {
        Hall hall = createValidHall();
        Team team = createValidTeam();

        TimeSlot timeSlot = TimeSlot.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(12, 0))
                .build();

        return TrainingSession.builder()
                .timeSlot(timeSlot)
                .hall(hall)
                .team(team)
                .build();
    }

    private Hall createValidHall() {
        Address address = Address.builder()
                .street("123 Rue Principale")
                .city("Paris")
                .postalCode("75000")
                .country("France")
                .build();

        Hall hall = Hall.builder()
                .name("Hall A")
                .address(address)
                .build();

        return hallRepository.save(hall); // Sauvegarder pour éviter les erreurs liées aux relations
    }

    private Team createValidTeam() {
        Team team = Team.builder()
                .gender(Gender.M)
                .category(Category.U15)
                .teamNumber(1)
                .build();

        return teamRepository.save(team); // Sauvegarder pour éviter les erreurs liées aux relations
    }
}