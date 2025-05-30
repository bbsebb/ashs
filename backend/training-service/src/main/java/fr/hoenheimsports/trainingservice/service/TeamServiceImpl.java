package fr.hoenheimsports.trainingservice.service;

import fr.hoenheimsports.trainingservice.exception.TeamAlreadyExistsException;
import fr.hoenheimsports.trainingservice.model.*;
import fr.hoenheimsports.trainingservice.repository.TeamRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * <p><b>TeamServiceImpl</b> implements the {@link TeamService} interface and provides business logic
 * for managing {@link Team} entities.</p>
 *
 * <p><b>Main Responsibilities:</b></p>
 * <ul>
 *     <li>Create and save a new Team entity.</li>
 *     <li>Retrieve a Team by its unique identifier.</li>
 *     <li>Fetch a paginated list of all Teams.</li>
 *     <li>Update an existing Team's information.</li>
 *     <li>Delete a Team by its unique identifier.</li>
 * </ul>
 *
 * <p>The class performs persistence operations using the {@link TeamRepository}, and throws
 * {@link EntityNotFoundException} when an operation involves a non-existing entity.</p>
 */
@Service
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final HallService hallService;
    private final CoachService coachService;
    private final RoleCoachService roleCoachService;

    public TeamServiceImpl(TeamRepository teamRepository, HallService hallService, CoachService coachService, RoleCoachService roleCoachService) {
        this.teamRepository = teamRepository;
        this.hallService = hallService;
        this.coachService = coachService;
        this.roleCoachService = roleCoachService;
    }

    /**
     * Creates a new Team entity and saves it to the repository.
     *
     * @param team the Team entity to be created and saved
     * @return the saved Team entity
     */
    @Override
    public Team createTeam(Team team) {
        if (isNotUniqueTeam(team)) {
            var messageError = """
                    Team already exists with combinaison of
                     Gender : %s
                     Category : %s
                     Team number : %d
                    """.formatted(team.getGender(), team.getCategory(), team.getTeamNumber());
            throw new TeamAlreadyExistsException(messageError);
        }
        return teamRepository.save(team);
    }

    /**
     * Retrieves a Team entity by its unique identifier.
     *
     * @param id the unique identifier of the Team to retrieve
     * @return the Team entity with the specified identifier
     * @throws EntityNotFoundException if no Team entity with the given identifier is found
     */
    @Override
    public Team getTeamById(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Team not found with id: " + id));
    }

    /**
     * Retrieves a paginated list of Team entities from the repository.
     *
     * @param pageable the pagination information, including page number, size, and sorting options
     * @return a page of Team entities based on the provided pagination information
     */
    @Override
    public Page<Team> getTeams(Pageable pageable) {
        return teamRepository.findAll(pageable);
    }


    /**
     * Retrieves a list of all Team entities from the repository.
     *
     * @return a list containing all Team entities in the repository
     */
    @Override
    public List<Team> getTeams() {
        return teamRepository.findAll();
    }

    /**
     * Updates an existing Team entity with new information.
     *
     * @param id          the unique identifier of the Team to update
     * @param updatedTeam the Team entity containing updated information
     * @return the updated Team entity after saving to the repository
     * @throws EntityNotFoundException if no Team entity with the given identifier is found
     */
    @Override
    @Transactional
    public Team updateTeam(Long id, Team updatedTeam) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Team not found with id: " + id));
        if (areEqual(team, updatedTeam)) {
            return team;
        }
        if (isNotUniqueTeam(updatedTeam)) {
            var messageError = """
                    Team already exists with combinaison of
                     Gender : %s
                     Category : %s
                     Team number : %d
                    """.formatted(updatedTeam.getGender(), updatedTeam.getCategory(), updatedTeam.getTeamNumber());
            throw new TeamAlreadyExistsException(messageError);
        }

        team.setTeamNumber(updatedTeam.getTeamNumber());
        team.setGender(updatedTeam.getGender());
        team.setCategory(updatedTeam.getCategory());
        return teamRepository.save(team);
    }

    private static boolean areEqual(Team team1, Team team2) {
        if (team1 == team2) return true;
        if (team1 == null || team2 == null) return false;

        return Objects.equals(team1.getGender(), team2.getGender()) &&
                Objects.equals(team1.getCategory(), team2.getCategory()) &&
                team1.getTeamNumber() == team2.getTeamNumber();
    }


    /**
     * Adds a training session to a specific team and associates it with a hall.
     *
     * @param teamId          the unique identifier of the team to which the training session will be added
     * @param hallId          the unique identifier of the hall where the training session will take place
     * @param trainingSession the training session entity to be added
     * @return the updated team entity after the training session is associated
     * @throws EntityNotFoundException if no team is found with the given {@code teamId} or no hall is found
     *                                 with the given {@code hallId}
     */
    @Override
    @Transactional
    public TrainingSession addTrainingSession(Long teamId, Long hallId, TrainingSession trainingSession) {
        Team team = getTeamById(teamId);
        Hall hall = hallService.getHallById(hallId);
        team.addTrainingSession(trainingSession);
        hall.addTrainingSession(trainingSession);
        return trainingSession;
    }


    @Override
    @Transactional
    public RoleCoach addRoleCoach(Long teamId, Long coachId, Role role) {
        Team team = getTeamById(teamId);
        Coach coach = coachService.getCoachById(coachId);
        return roleCoachService.createRoleCoach(role, coach, team);
    }


    /**
     * Deletes a Team entity by its unique identifier.
     *
     * @param id the unique identifier of the Team to delete
     * @throws EntityNotFoundException if no Team entity with the given identifier is found
     */
    @Override
    @Transactional
    public void deleteTeam(Long id) {
        if (!teamRepository.existsById(id)) {
            throw new EntityNotFoundException("Team not found with id: " + id);
        }
        teamRepository.deleteById(id);
    }

    private boolean isNotUniqueTeam(Team team) {
        return isNotUniqueTeam(team.getGender(), team.getCategory(), team.getTeamNumber());
    }

    @Override
    public boolean isNotUniqueTeam(Gender gender, Category category, int teamNumber) {
        return teamRepository.existsByGenderAndCategoryAndTeamNumber(gender, category, teamNumber);
    }


}