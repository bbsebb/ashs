package fr.hoenheimsports.trainingservice.repository;

import fr.hoenheimsports.trainingservice.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import jakarta.validation.ConstraintViolationException;

import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.*;

@Testcontainers
@DataJpaTest
class HallRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    HallRepository hallRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private TrainingSessionRepository trainingSessionRepository;


    @Test
    void testValidHall() {

        // Hall valide
        Hall hall = getValidHall();
        Hall savedHall = hallRepository.save(hall);
        assertThat(savedHall.getId()).isNotNull();
        assertThat(savedHall.getName()).isEqualTo(hall.getName());
        assertThat(savedHall.getAddress().getStreet()).isEqualTo(hall.getAddress().getStreet());
        assertThat(savedHall.getAddress().getCity()).isEqualTo(hall.getAddress().getCity());
        assertThat(savedHall.getAddress().getPostalCode()).isEqualTo(hall.getAddress().getPostalCode());
        assertThat(savedHall.getAddress().getCountry()).isEqualTo(hall.getAddress().getCountry());
    }

    @Test
    void testHallNameBlankConstraint() {
        // Hall avec un nom vide
        Address address = Address.builder()
                .street("10 Rue des Fleurs")
                .city("Paris")
                .postalCode("75001")
                .country("France")
                .build();

        Hall hall = Hall.builder()
                .name("") // nom vide
                .address(address)
                .build();

        assertThatThrownBy(() -> hallRepository.saveAndFlush(hall))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("ne doit pas être vide");
    }

    @Test
    void testHallNameSizeConstraint() {
        // Hall avec un nom trop long
        Address address = Address.builder()
                .street("10 Rue des Fleurs")
                .city("Paris")
                .postalCode("75001")
                .country("France")
                .build();

        Hall hall = Hall.builder()
                .name("A".repeat(51)) // nom de 51 caractères
                .address(address)
                .build();

        assertThatThrownBy(() -> hallRepository.saveAndFlush(hall))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("La nom de la salle ne doit pas dépasser 50 caractères");
    }

    @Test
    void testAddressNotNullConstraint() {
        // Hall sans adresse
        Hall hall = Hall.builder()
                .name("Gymnase Central")
                .address(null) // Adresse manquante
                .build();

        assertThatThrownBy(() -> hallRepository.saveAndFlush(hall))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("ne doit pas être nul");
    }

    @Test
    void testAddressStreetBlankConstraint() {
        // Adresse avec une rue vide
        Address address = Address.builder()
                .street("") // Rue vide
                .city("Paris")
                .postalCode("75001")
                .country("France")
                .build();

        Hall hall = Hall.builder()
                .name("Gymnase Central")
                .address(address)
                .build();

        assertThatThrownBy(() -> hallRepository.saveAndFlush(hall))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("La rue est obligatoire");
    }

    @Test
    void testAddressStreetSizeConstraint() {
        // Adresse avec une rue trop longue
        Address address = Address.builder()
                .street("A".repeat(101)) // Rue de 101 caractères
                .city("Paris")
                .postalCode("75001")
                .country("France")
                .build();

        Hall hall = Hall.builder()
                .name("Gymnase Central")
                .address(address)
                .build();

        assertThatThrownBy(() -> hallRepository.saveAndFlush(hall))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("La rue ne doit pas dépasser 100 caractères");
    }

    @Test
    void testAddressCityBlankConstraint() {
        // Adresse avec une ville vide
        Address address = Address.builder()
                .street("10 Rue des Fleurs")
                .city("") // Ville vide
                .postalCode("75001")
                .country("France")
                .build();

        Hall hall = Hall.builder()
                .name("Gymnase Central")
                .address(address)
                .build();

        assertThatThrownBy(() -> hallRepository.saveAndFlush(hall))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("La ville est obligatoire");
    }

    @Test
    void testAddressPostalCodePatternConstraint() {
        // Adresse avec un code postal invalide
        Address address = Address.builder()
                .street("10 Rue des Fleurs")
                .city("Paris")
                .postalCode("7500A") // Code postal invalide
                .country("France")
                .build();

        Hall hall = Hall.builder()
                .name("Gymnase Central")
                .address(address)
                .build();

        assertThatThrownBy(() -> hallRepository.saveAndFlush(hall))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("Le code postal doit être composé de 5 chiffres");
    }

    @Test
    void testAddressCountryBlankConstraint() {
        // Adresse avec un pays vide
        Address address = Address.builder()
                .street("10 Rue des Fleurs")
                .city("Paris")
                .postalCode("75001")
                .country("") // Pays vide
                .build();

        Hall hall = Hall.builder()
                .name("Gymnase Central")
                .address(address)
                .build();

        assertThatThrownBy(() -> hallRepository.saveAndFlush(hall))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("Le pays est obligatoire");
    }

    @Test
    void testAddressCountrySizeConstraint() {
        // Adresse avec un pays trop long
        Address address = Address.builder()
                .street("10 Rue des Fleurs")
                .city("Paris")
                .postalCode("75001")
                .country("A".repeat(51)) // Pays de 51 caractères
                .build();

        Hall hall = Hall.builder()
                .name("Gymnase Central")
                .address(address)
                .build();

        assertThatThrownBy(() -> hallRepository.saveAndFlush(hall))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("Le pays ne doit pas dépasser 50 caractères");
    }

    @Test
    void testValidTeamWithInvalidTrainingSession() {
        // Arrange
        Hall hall = getValidHall();

        hall.getTrainingSessions().getFirst().setTeam(null);

        // Act & Assert
        assertThatThrownBy(() -> hallRepository.saveAndFlush(hall))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("ne doit pas être nul");
    }

    @Test
    void testCascadePersistTrainingSessions() {
        // Arrange
        Hall hall = getValidHall();
        hall = hallRepository.save(hall);
        Team team = Team.builder()
                .teamNumber(2)
                .gender(Gender.M)
                .category(Category.U11)
                .build();
        team = teamRepository.save(team);
        TrainingSession trainingSession1 = TrainingSession.builder()
                .timeSlot(new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(12, 0)))
                .team(team)
                .build();
        hall.addTrainingSession(trainingSession1);


        // Act
        Hall savedTeam = hallRepository.save(hall);

        // Assert
        assertThat(savedTeam.getTrainingSessions()).hasSize(2); // Vérifie que les 2 sessions sont liées à l'équipe
        assertThat(savedTeam.getTrainingSessions().get(0).getId()).isNotNull(); // Vérifie leur persistance
        assertThat(savedTeam.getTrainingSessions().get(1).getId()).isNotNull();
    }



    @Test
    void testCascadeMergeTrainingSessions() {
        // Arrange
        Hall hall = getValidHall();
        Hall savedHall = hallRepository.save(hall);
        savedHall.getTrainingSessions().getFirst().getTimeSlot().setDayOfWeek(DayOfWeek.TUESDAY);
        // Act
        Hall updatedHall = hallRepository.save(savedHall);
        // Assert
        assertThat(updatedHall.getTrainingSessions()).hasSize(1);
        assertThat(updatedHall).isNotNull();
        assertThat(updatedHall.getId()).isNotNull();
        assertThat(updatedHall.getTrainingSessions()).hasSize(1);
        assertThat(updatedHall.getTrainingSessions().getFirst().getId()).isNotNull();
        assertThat(updatedHall.getTrainingSessions().getFirst().getHall()).isEqualTo(savedHall);
        assertThat(updatedHall.getTrainingSessions().getFirst().getTimeSlot().getDayOfWeek()).isEqualTo(DayOfWeek.TUESDAY);
        assertThat(updatedHall.getTrainingSessions().getFirst().getTeam()).isEqualTo(hall.getTrainingSessions().getFirst().getTeam());

    }

    @Test
    void testOrphanRemovalForTrainingSessions() {
        // Arrange
        Hall hall = getValidHall();
        hall = hallRepository.save(hall);
        Team team = Team.builder()
                .teamNumber(2)
                .gender(Gender.M)
                .category(Category.U11)
                .build();
        team = teamRepository.save(team);
        TrainingSession trainingSession1 = TrainingSession.builder()
                .timeSlot(new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(12, 0)))
                .team(team)
                .build();
        hall.addTrainingSession(trainingSession1);
        Hall savedHall = hallRepository.save(hall);
        var persistedTrainingSession1 = savedHall.getTrainingSessions().getFirst();
        assertThat(trainingSessionRepository.existsById(persistedTrainingSession1.getId())).isTrue();
        // Act
        hallRepository.delete(savedHall);

        // Assert
        assertThat(trainingSessionRepository.existsById(persistedTrainingSession1.getId())).isFalse();

    }

    private Hall getValidHall() {
        // Adresse valide
        Address address = Address.builder()
                .street("10 Rue des Fleurs")
                .city("Paris")
                .postalCode("75001")
                .country("France")
                .build();
        TrainingSession trainingSession1 = TrainingSession.builder()
                .team(getValidTeam())
                .timeSlot(new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(12, 0)))
                .build();

        // Hall valide
        Hall hall = Hall.builder()
                .name("Gymnase Central")
                .address(address)
                .build();
        hall.addTrainingSession(trainingSession1);
        return hall;
    }



    private Team getValidTeam() {
        return teamRepository.save(Team.builder()
                .teamNumber(1)
                .gender(Gender.M)
                .category(Category.U11)
                .build());
    }


}