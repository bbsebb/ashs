package fr.hoenheimsports.trainingservice.repository;

import fr.hoenheimsports.trainingservice.model.*;
import jakarta.validation.ConstraintViolationException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Testcontainers
@DataJpaTest
class TeamRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private HallRepository hallRepository;
    @Autowired
    private TrainingSessionRepository trainingSessionRepository;


    @Test
    void testValidTeam() {
        // Arrange
        Team team = getValidTeam();


        // Act
        Team savedTeam = teamRepository.save(team);

        // Assert
        assertThat(savedTeam).isNotNull();
        assertThat(savedTeam.getId()).isNotNull();
        assertThat(savedTeam.getGender()).isEqualTo(team.getGender());
        assertThat(savedTeam.getCategory()).isEqualTo(team.getCategory());
        assertThat(savedTeam.getTeamNumber()).isEqualTo(team.getTeamNumber());
        assertThat(savedTeam.getTrainingSessions()).hasSize(1);
        assertThat(savedTeam.getTrainingSessions().getFirst().getId()).isNotNull();
        assertThat(savedTeam.getTrainingSessions().getFirst().getTeam()).isEqualTo(savedTeam);
        assertThat(savedTeam.getTrainingSessions().getFirst().getTimeSlot()).isEqualTo(team.getTrainingSessions().getFirst().getTimeSlot());
        assertThat(savedTeam.getTrainingSessions().getFirst().getHall()).isEqualTo(team.getTrainingSessions().getFirst().getHall());
    }

    @Test
    void testNotNullConstraintForGender() {
        // Arrange
        Team team = getTeam(1, null, Category.U18); // Gender nul

        // Act & Assert
        assertThatThrownBy(() -> teamRepository.saveAndFlush(team))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("ne doit pas être nul");
    }

    @Test
    void testNotNullConstraintForCategory() {
        // Arrange
        Team team = getTeam(1, Gender.M, null); // Category nul

        // Act & Assert
        assertThatThrownBy(() -> teamRepository.saveAndFlush(team))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("ne doit pas être nul");
    }

    @Test
    void testPositiveConstraintForTeamNumber() {
        // Arrange
        Team team = getTeam(-1, Gender.M, Category.U18); // Numéro d'équipe négatif

        // Act & Assert
        assertThatThrownBy(() -> teamRepository.saveAndFlush(team))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("doit être supérieur à 0");
    }

    @Test
    void testValidTeamWithInvalidTrainingSession() {
        // Arrange
        Team team = getValidTeam();
        team.getTrainingSessions().getFirst().setTeam(null);

        // Act & Assert
        assertThatThrownBy(() -> teamRepository.saveAndFlush(team))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("ne doit pas être nul");
    }

    @Test
    void testCascadePersistTrainingSessions() {
        // Arrange
        Team team = getValidTeam();
        Hall hall = Hall.builder()
                .name("Gymnase Central 2")
                .address(Address.builder()
                        .street("10 Rue des Fleurs")
                        .city("Paris")
                        .postalCode("75001")
                        .country("France")
                        .build())
                .build();
        hall = hallRepository.save(hall);
        TrainingSession trainingSession1 = TrainingSession.builder()
                .timeSlot(new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(12, 0)))
                .hall(hall)
                .build();
        team.addTrainingSession(trainingSession1);


        // Act
        Team savedTeam = teamRepository.save(team);

        // Assert
        assertThat(savedTeam.getTrainingSessions()).hasSize(2); // Vérifie que les 2 sessions sont liées à l'équipe
        assertThat(savedTeam.getTrainingSessions().get(0).getId()).isNotNull(); // Vérifie leur persistance
        assertThat(savedTeam.getTrainingSessions().get(1).getId()).isNotNull();
    }

    @Test
    void testCascadeMergeTrainingSessions() {
        // Arrange
        Team team = getValidTeam();
        Team savedTeam = teamRepository.save(team);
        savedTeam.getTrainingSessions().getFirst().getTimeSlot().setDayOfWeek(DayOfWeek.TUESDAY);
        // Act
        Team updatedTeam = teamRepository.save(savedTeam);
        // Assert
        assertThat(updatedTeam.getTrainingSessions()).hasSize(1);
        assertThat(updatedTeam).isNotNull();
        assertThat(updatedTeam.getId()).isNotNull();
        assertThat(updatedTeam.getGender()).isEqualTo(team.getGender());
        assertThat(updatedTeam.getCategory()).isEqualTo(team.getCategory());
        assertThat(updatedTeam.getTeamNumber()).isEqualTo(team.getTeamNumber());
        assertThat(updatedTeam.getTrainingSessions()).hasSize(1);
        assertThat(updatedTeam.getTrainingSessions().getFirst().getId()).isNotNull();
        assertThat(updatedTeam.getTrainingSessions().getFirst().getTeam()).isEqualTo(savedTeam);
        assertThat(updatedTeam.getTrainingSessions().getFirst().getTimeSlot().getDayOfWeek()).isEqualTo(DayOfWeek.TUESDAY);
        assertThat(updatedTeam.getTrainingSessions().getFirst().getHall()).isEqualTo(team.getTrainingSessions().getFirst().getHall());

    }

    @Test
    void testOrphanRemovalForTrainingSessions() {
        // Arrange
        Team team = getValidTeam();
        Hall hall = Hall.builder()
                .name("Gymnase Central 2")
                .address(Address.builder()
                        .street("10 Rue des Fleurs")
                        .city("Paris")
                        .postalCode("75001")
                        .country("France")
                        .build())
                .build();
        hall = hallRepository.save(hall);
        TrainingSession trainingSession1 = TrainingSession.builder()
                .timeSlot(new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(12, 0)))
                .hall(hall)
                .build();
        team.addTrainingSession(trainingSession1);
        Team savedTeam = teamRepository.save(team);
        var persistedTrainingSession1 = savedTeam.getTrainingSessions().getFirst();
        var persistedTrainingSession2 = savedTeam.getTrainingSessions().getLast();
        assertThat(trainingSessionRepository.existsById(persistedTrainingSession1.getId())).isTrue();
        assertThat(trainingSessionRepository.existsById(persistedTrainingSession2.getId())).isTrue();
        // Act
        teamRepository.delete(savedTeam);

        // Assert
        assertThat(trainingSessionRepository.existsById(persistedTrainingSession1.getId())).isFalse();
        assertThat(trainingSessionRepository.existsById(persistedTrainingSession2.getId())).isFalse();
    }





    private Team getTeam(int teamNumber, Gender gender, Category category) {
        return Team.builder()
                .teamNumber(teamNumber)
                .gender(gender)
                .category(category)
                .build();
    }

    private Team getValidTeam() {
        Team team = getTeam(1, Gender.M, Category.U18);

        Hall hall = getHall();
        TrainingSession trainingSession = TrainingSession.builder()
                .timeSlot(new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(12, 0)))
                .hall(hall)
                .build();

        team.addTrainingSession(trainingSession);
        return team;
    }

    private @NotNull Hall getHall() {
        Address address = Address.builder()
                .street("10 Rue des Fleurs")
                .city("Paris")
                .postalCode("75001")
                .country("France")
                .build();
        Hall hall = Hall.builder()
                .name("Gymnase Central")
                .address(address)
                .build();
        hall = hallRepository.save(hall);
        return hall;
    }
}