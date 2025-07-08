package fr.hoenheimsports.trainingservice.service;

import fr.hoenheimsports.trainingservice.exception.TeamAlreadyExistsException;
import fr.hoenheimsports.trainingservice.model.*;
import fr.hoenheimsports.trainingservice.repository.TeamRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
        log.info("Création d'une nouvelle équipe: {}/{}/{}", 
                team.getGender(), team.getCategory(), team.getTeamNumber());

        if (isNotUniqueTeam(team)) {
            log.warn("Tentative de création d'une équipe déjà existante: {}/{}/{}", 
                    team.getGender(), team.getCategory(), team.getTeamNumber());
            var messageError = """
                    Team already exists with combinaison of
                     Gender : %s
                     Category : %s
                     Team number : %d
                    """.formatted(team.getGender(), team.getCategory(), team.getTeamNumber());
            throw new TeamAlreadyExistsException(messageError);
        }

        Team savedTeam = teamRepository.save(team);
        log.info("Équipe créée avec succès, ID: {}", savedTeam.getId());
        return savedTeam;
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
        log.debug("Recherche de l'équipe avec l'ID: {}", id);
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Équipe introuvable avec l'ID: {}", id);
                    return new EntityNotFoundException("Team not found with id: " + id);
                });
        log.debug("Équipe trouvée: {}/{}/{}", 
                team.getGender(), team.getCategory(), team.getTeamNumber());
        return team;
    }

    /**
     * Retrieves a paginated list of Team entities from the repository.
     *
     * @param pageable the pagination information, including page number, size, and sorting options
     * @return a page of Team entities based on the provided pagination information
     */
    @Override
    public Page<Team> getTeams(Pageable pageable) {
        log.debug("Récupération des équipes paginées: page={}, taille={}, tri={}", 
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        Page<Team> teams = teamRepository.findAll(pageable);
        log.debug("Nombre d'équipes récupérées: {}", teams.getNumberOfElements());
        return teams;
    }


    /**
     * Retrieves a list of all Team entities from the repository.
     *
     * @return a list containing all Team entities in the repository
     */
    @Override
    public List<Team> getTeams() {
        log.debug("Récupération de toutes les équipes");
        List<Team> teams = teamRepository.findAll();
        log.debug("Nombre total d'équipes récupérées: {}", teams.size());
        return teams;
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
        log.info("Mise à jour de l'équipe avec l'ID: {}", id);
        log.debug("Nouvelles informations: genre={}, catégorie={}, numéro={}", 
                updatedTeam.getGender(), updatedTeam.getCategory(), updatedTeam.getTeamNumber());

        Team team = teamRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Tentative de mise à jour d'une équipe inexistante, ID: {}", id);
                    return new EntityNotFoundException("Team not found with id: " + id);
                });

        log.debug("Équipe trouvée pour mise à jour: {}/{}/{}", 
                team.getGender(), team.getCategory(), team.getTeamNumber());

        if (areEqual(team, updatedTeam)) {
            log.debug("Aucune modification nécessaire, les données sont identiques");
            return team;
        }

        if (isNotUniqueTeam(updatedTeam)) {
            log.warn("Tentative de mise à jour vers une équipe déjà existante: {}/{}/{}", 
                    updatedTeam.getGender(), updatedTeam.getCategory(), updatedTeam.getTeamNumber());
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

        Team savedTeam = teamRepository.save(team);
        log.info("Équipe mise à jour avec succès, ID: {}", savedTeam.getId());
        return savedTeam;
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
        log.info("Ajout d'une séance d'entraînement à l'équipe ID: {} dans la salle ID: {}", teamId, hallId);

        if (trainingSession.getTimeSlot() != null) {
            log.debug("Détails de la séance: jour={}, heure de début={}, heure de fin={}", 
                    trainingSession.getTimeSlot().getDayOfWeek(), 
                    trainingSession.getTimeSlot().getStartTime(), 
                    trainingSession.getTimeSlot().getEndTime());
        }

        Team team = getTeamById(teamId);
        Hall hall = hallService.getHallById(hallId);

        log.debug("Association de la séance à l'équipe: {}/{}/{}", 
                team.getGender(), team.getCategory(), team.getTeamNumber());
        team.addTrainingSession(trainingSession);

        log.debug("Association de la séance à la salle: {}", hall.getName());
        hall.addTrainingSession(trainingSession);

        log.info("Séance d'entraînement ajoutée avec succès");
        return trainingSession;
    }


    @Override
    @Transactional
    public RoleCoach addRoleCoach(Long teamId, Long coachId, Role role) {
        log.info("Ajout d'un rôle de coach ({}) à l'équipe ID: {} pour le coach ID: {}", role, teamId, coachId);

        Team team = getTeamById(teamId);
        log.debug("Équipe trouvée: {}/{}/{}", team.getGender(), team.getCategory(), team.getTeamNumber());

        Coach coach = coachService.getCoachById(coachId);
        log.debug("Coach trouvé: {} {}", coach.getName(), coach.getSurname());

        RoleCoach roleCoach = roleCoachService.createRoleCoach(role, coach, team);
        log.info("Rôle de coach ajouté avec succès, ID: {}", roleCoach.getId());
        return roleCoach;
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
        log.info("Suppression de l'équipe avec l'ID: {}", id);
        if (!teamRepository.existsById(id)) {
            log.warn("Tentative de suppression d'une équipe inexistante, ID: {}", id);
            throw new EntityNotFoundException("Team not found with id: " + id);
        }
        teamRepository.deleteById(id);
        log.info("Équipe supprimée avec succès, ID: {}", id);
    }

    private boolean isNotUniqueTeam(Team team) {
        log.debug("Vérification de l'unicité de l'équipe: {}/{}/{}", 
                team.getGender(), team.getCategory(), team.getTeamNumber());
        return isNotUniqueTeam(team.getGender(), team.getCategory(), team.getTeamNumber());
    }

    @Override
    public boolean isNotUniqueTeam(Gender gender, Category category, int teamNumber) {
        log.debug("Vérification de l'unicité de l'équipe avec les paramètres: genre={}, catégorie={}, numéro={}", 
                gender, category, teamNumber);
        boolean exists = teamRepository.existsByGenderAndCategoryAndTeamNumber(gender, category, teamNumber);
        log.debug("Résultat de la vérification d'unicité: équipe {} existante", exists ? "déjà" : "non");
        return exists;
    }


}
