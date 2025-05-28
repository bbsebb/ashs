package fr.hoenheimsports.trainingservice.service;

import fr.hoenheimsports.trainingservice.exception.TeamAlreadyExistsException;
import fr.hoenheimsports.trainingservice.model.Category;
import fr.hoenheimsports.trainingservice.model.Gender;
import fr.hoenheimsports.trainingservice.model.Team;
import fr.hoenheimsports.trainingservice.repository.TeamRepository;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TeamServiceImplTest {

    @Mock
    private TeamRepository teamRepository;

    @InjectMocks
    private TeamServiceImpl teamService;

    @Test
    void createTeam_shouldReturnTeam_whenValidRequest() {
        // Arrange
        Team team = getTeam();
        when(teamRepository.save(team)).thenReturn(team);

        // Act
        Team result = teamService.createTeam(team);

        // Assert
        assertThat(result).isNotNull().isEqualTo(team);
        verify(teamRepository, times(1)).save(team);
    }

    @Test
    void createTeam_shouldThrowTeamAlreadyExistsException_whenTeamNumberAndGenderAndCategoryAreAlreadyUsed() {
        // Arrange
        Team team = getTeam();
        when(teamRepository.existsByGenderAndCategoryAndTeamNumber(team.getGender(), team.getCategory(), team.getTeamNumber())).thenReturn(true);
        var messageError = """
                    Team already exists with combinaison of
                     Gender : %s
                     Category : %s
                     Team number : %d
                    """.formatted(team.getGender(), team.getCategory(), team.getTeamNumber());
        // Act & Assert
        assertThatThrownBy(() -> teamService.createTeam(team))
                .isInstanceOf(TeamAlreadyExistsException.class)
                .hasMessage(messageError);
        verify(teamRepository, times(0)).save(team);
    }

    @Test
    void getTeamById_shouldReturnTeam_whenIdExists() {
        // Arrange
        Long teamId = 1L;
        Team team = getTeam(teamId);
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));

        // Act
        Team result = teamService.getTeamById(teamId);

        // Assert
        assertThat(result).isNotNull().isEqualTo(team);
        verify(teamRepository, times(1)).findById(teamId);
    }

    @Test
    void getTeamById_shouldThrowEntityNotFoundException_whenIdDoesNotExist() {
        // Arrange
        Long teamId = 1L;
        when(teamRepository.findById(teamId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> teamService.getTeamById(teamId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Team not found with id: " + teamId);
        verify(teamRepository, times(1)).findById(teamId);
    }

    @Test
    void getTeams_shouldReturnPageOfTeams_whenValidRequest() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Team> teamList = List.of(getTeam(), getTeam(2L));
        Page<Team> teamPage = new PageImpl<>(teamList, pageable, teamList.size());
        when(teamRepository.findAll(pageable)).thenReturn(teamPage);

        // Act
        Page<Team> result = teamService.getTeams(pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2).containsExactlyElementsOf(teamList);
        verify(teamRepository).findAll(pageable);
    }

    @Test
    void updateTeam_shouldReturnUpdatedTeam_whenValidIdAndRequest() {
        // Arrange
        Long teamId = 1L;
        Team existingTeam = Mockito.spy(getTeam(teamId));
        Team updatedTeam = getTeam(teamId);
        updatedTeam.setTeamNumber(42);

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(existingTeam));
        when(teamRepository.save(existingTeam)).thenReturn(updatedTeam);

        // Act
        Team result = teamService.updateTeam(teamId, updatedTeam);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getTeamNumber()).isEqualTo(42);
        verify(existingTeam).setTeamNumber(updatedTeam.getTeamNumber());
        verify(existingTeam).setGender(updatedTeam.getGender());
        verify(existingTeam).setCategory(updatedTeam.getCategory());
        verifyNoMoreInteractions(existingTeam);
        verify(teamRepository).findById(teamId);
        verify(teamRepository).save(existingTeam);
    }

    @Test
    void updateTeam_shouldThrowEntityNotFoundException_whenIdDoesNotExist() {
        // Arrange
        Long teamId = 1L;
        Team updatedTeam = getTeam(teamId);
        when(teamRepository.findById(teamId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> teamService.updateTeam(teamId, updatedTeam))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Team not found with id: "+teamId);
        verify(teamRepository, times(1)).findById(teamId);
    }

    @Test
    void updateTeam_shouldThrowTeamAlreadyExistsException_whenTeamNumberAndGenderAndCategoryAreAlreadyUsed() {
        // Arrange
        Long teamId = 1L;
        Team updatedTeam = getTeam(teamId);

        when(teamRepository.existsByGenderAndCategoryAndTeamNumber(updatedTeam.getGender(), updatedTeam.getCategory(), updatedTeam.getTeamNumber())).thenReturn(true);
        var messageError = """
                    Team already exists with combinaison of
                     Gender : %s
                     Category : %s
                     Team number : %s
                    """.formatted(updatedTeam.getGender(), updatedTeam.getCategory(), updatedTeam.getTeamNumber());
        // Act & Assert
        assertThatThrownBy(() -> teamService.updateTeam(teamId, updatedTeam))
                .isInstanceOf(TeamAlreadyExistsException.class)
                .hasMessage(messageError);
        verify(teamRepository, times(0)).findById(teamId);
    }

    @Test
    void deleteTeam_shouldDeleteTeam_whenIdExists() {
        // Arrange
        Long teamId = 1L;
        when(teamRepository.existsById(teamId)).thenReturn(true);

        // Act
        teamService.deleteTeam(teamId);

        // Assert
        verify(teamRepository, times(1)).existsById(teamId);
        verify(teamRepository, times(1)).deleteById(teamId);
    }

    @Test
    void deleteTeam_shouldThrowEntityNotFoundException_whenIdDoesNotExist() {
        // Arrange
        Long teamId = 1L;
        when(teamRepository.existsById(teamId)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> teamService.deleteTeam(teamId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Team not found with id: " + teamId);
        verify(teamRepository, times(1)).existsById(teamId);
        verify(teamRepository, never()).deleteById(teamId);
    }

    @Test
    void addTeamToTrainingSession_shouldAddTeamToTrainingSession_whenValidIdAndTrainingSessionId() {
        //TODO
    }

    // Méthodes utilitaires pour créer des entités de test
    private static Team getTeam(Long teamId) {
        return Team.builder()
                .id(teamId)
                .gender(Gender.M)
                .category(Category.SENIOR)
                .teamNumber(1)
                .build();
    }

    private static Team getTeam() {
        return getTeam(1L);
    }
}