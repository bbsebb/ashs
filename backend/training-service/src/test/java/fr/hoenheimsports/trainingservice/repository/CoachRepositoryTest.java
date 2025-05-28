package fr.hoenheimsports.trainingservice.repository;

import fr.hoenheimsports.trainingservice.model.Coach;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
class CoachRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    CoachRepository coachRepository;

    @Test
    void testValidCoach() {
        // Arrange
        Coach coach = Coach.builder()
                .name("John")
                .surname("Doe")
                .email("john.doe@example.com")
                .phone("+33123456789")
                .build();

        // Act
        Coach savedCoach = coachRepository.save(coach);

        // Assert
        assertThat(savedCoach.getId()).isNotNull();
        assertThat(savedCoach.getName()).isEqualTo("John");
        assertThat(savedCoach.getSurname()).isEqualTo("Doe");
        assertThat(savedCoach.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(savedCoach.getPhone()).isEqualTo("+33123456789");
        //todo faire les test pour les relations bidirectionnelles
    }

    @Test
    void testNameBlankConstraint() {
        // Arrange
        Coach coach = Coach.builder()
                .name("")
                .surname("Doe")
                .email("john.doe@example.com")
                .phone("+33123456789")
                .build();

        // Act & Assert
        assertThatThrownBy(() -> coachRepository.saveAndFlush(coach))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("ne doit pas être vide");
    }

    @Test
    void testSurnameBlankConstraint() {
        // Arrange
        Coach coach = Coach.builder()
                .name("John")
                .surname("")
                .email("john.doe@example.com")
                .phone("+33123456789")
                .build();

        // Act & Assert
        assertThatThrownBy(() -> coachRepository.saveAndFlush(coach))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("ne doit pas être vide");
    }

    @Test
    void testEmailFormatConstraint() {
        // Arrange
        Coach coach = Coach.builder()
                .name("John")
                .surname("Doe")
                .email("email-invalide")
                .phone("+33123456789")
                .build();

        // Act & Assert
        assertThatThrownBy(() -> coachRepository.saveAndFlush(coach))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("doit être une adresse électronique syntaxiquement correcte");
    }

    @Test
    void testPhonePatternConstraint() {
        // Arrange
        Coach coach = Coach.builder()
                .name("John")
                .surname("Doe")
                .email("john.doe@example.com")
                .phone("12345")
                .build();

        // Act & Assert
        assertThatThrownBy(() -> coachRepository.saveAndFlush(coach))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("Numéro de téléphone invalide");
    }

    @Test
    void testNameNullConstraint() {
        // Arrange
        Coach coach = Coach.builder()
                .name(null)
                .surname("Doe")
                .email("john.doe@example.com")
                .phone("+33123456789")
                .build();

        // Act & Assert
        assertThatThrownBy(() -> coachRepository.saveAndFlush(coach))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("ne doit pas être vide");
    }

    @Test
    void testSurnameNullConstraint() {
        // Arrange
        Coach coach = Coach.builder()
                .name("John")
                .surname(null)
                .email("john.doe@example.com")
                .phone("+33123456789")
                .build();

        // Act & Assert
        assertThatThrownBy(() -> coachRepository.saveAndFlush(coach))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("ne doit pas être vide");
    }

    @Test
    void testEmailNullConstraint() {
        // Arrange
        Coach coach = Coach.builder()
                .name("John")
                .surname("Doe")
                .email(null)
                .phone("+33123456789")
                .build();

        // Act & Assert
        assertThatThrownBy(() -> coachRepository.saveAndFlush(coach))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("ne doit pas être nul");
    }

    @Test
    void testPhoneNullConstraint() {
        // Arrange
        Coach coach = Coach.builder()
                .name("John")
                .surname("Doe")
                .email("john.doe@example.com")
                .phone(null)
                .build();

        // Act & Assert
        assertThatThrownBy(() -> coachRepository.saveAndFlush(coach))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("ne doit pas être nul");
    }
}