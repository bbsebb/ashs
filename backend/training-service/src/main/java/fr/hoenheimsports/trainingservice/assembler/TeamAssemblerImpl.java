package fr.hoenheimsports.trainingservice.assembler;

import fr.hoenheimsports.trainingservice.controller.RoleCoachControllerImpl;
import fr.hoenheimsports.trainingservice.controller.TeamControllerImpl;
import fr.hoenheimsports.trainingservice.controller.TrainingSessionControllerImpl;
import fr.hoenheimsports.trainingservice.dto.response.TeamDTOResponse;
import fr.hoenheimsports.trainingservice.mapper.TeamMapper;
import fr.hoenheimsports.trainingservice.model.Team;
import fr.hoenheimsports.trainingservice.service.UserSecurityService;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.*;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

/**
 * Implementation of TeamAssembler that converts Team entities to HATEOAS-compliant representations.
 * This class handles the creation of links and affordances for Team resources.
 */
@Component
public class TeamAssemblerImpl extends AbstractAssembler<Team, EntityModel<TeamDTOResponse>> implements TeamAssembler {

    public static final String ADMIN_ROLE = "ADMIN";

    private final TeamMapper teamMapper;
    private final UserSecurityService userSecurityService;

    /**
     * Constructor for TeamAssemblerImpl.
     *
     * @param pagedResourcesAssembler Assembler for creating paged resources
     * @param teamMapper              Mapper for converting Team entities to DTOs
     * @param userSecurityService     Service for checking user roles and permissions
     */
    public TeamAssemblerImpl(PagedResourcesAssembler<Team> pagedResourcesAssembler, TeamMapper teamMapper, UserSecurityService userSecurityService) {
        super(pagedResourcesAssembler);
        this.teamMapper = teamMapper;
        this.userSecurityService = userSecurityService;
    }

    /**
     * Converts a Team entity to an EntityModel with appropriate links and affordances.
     *
     * @param team The Team entity to convert
     * @return EntityModel containing the Team DTO and related links
     */
    @NonNull
    @Override
    public EntityModel<TeamDTOResponse> toModel(@NonNull Team team) {
        Assert.notNull(team, "Team must not be null!");
        TeamDTOResponse teamDTOResponse = teamMapper.toDto(team);

        // Create links for each TrainingSession using HATEOAS
        List<Link> trainingSessionLinks = team.getTrainingSessions().stream()
                .map(trainingSession ->
                        linkTo(methodOn(TrainingSessionControllerImpl.class).getTrainingSessionById(trainingSession.getId()))
                                .withRel("trainingSessionsList"))
                .toList();

        // Create links for each RoleCoach using HATEOAS
        List<Link> roleCoachLinks = team.getRoleCoaches().stream()
                .map(roleCoach ->
                        linkTo(methodOn(RoleCoachControllerImpl.class).getRoleCoachById(roleCoach.getId()))
                                .withRel("roleCoachesList"))
                .toList();

        // Create the entity model with all links
        EntityModel<TeamDTOResponse> entityModel = EntityModel.of(teamDTOResponse);
        Link teamsLink = linkTo(methodOn(TeamControllerImpl.class).getTeams(null)).withRel("teams");
        Link selfLink = linkTo(methodOn(TeamControllerImpl.class).getTeamById(teamDTOResponse.id()))
                .withSelfRel()
                .andAffordances(createTeamAffordances(teamDTOResponse));

        entityModel.add(selfLink, teamsLink);
        entityModel.add(trainingSessionLinks);
        entityModel.add(roleCoachLinks);

        return entityModel;
    }

    /**
     * Creates affordances for a specific team.
     * Affordances represent available actions that can be performed on the resource.
     *
     * @param teamDTOResponse The team DTO for which to create affordances
     * @return List of affordances available for the team
     */
    private List<Affordance> createTeamAffordances(TeamDTOResponse teamDTOResponse) {
        List<Affordance> affordances = new ArrayList<>();
        if (userSecurityService.hasRole(ADMIN_ROLE)) {
            affordances.add(afford(methodOn(TeamControllerImpl.class).deleteTeam(teamDTOResponse.id())));
            affordances.add(afford(methodOn(TeamControllerImpl.class).deleteTeam(teamDTOResponse.id())));
            affordances.add(afford(methodOn(TeamControllerImpl.class).updateTeam(teamDTOResponse.id(), null)));
            affordances.add(afford(methodOn(TeamControllerImpl.class).addTrainingSession(teamDTOResponse.id(), null)));
            affordances.add(afford(methodOn(TeamControllerImpl.class).addRoleCoach(teamDTOResponse.id(), null)));
        }
        return affordances;
    }


    @NonNull
    @Override
    public CollectionModel<EntityModel<TeamDTOResponse>> toCollectionModel(@NonNull Iterable<? extends Team> entities) {
        Assert.notNull(entities, "Entities must not be null!");

        CollectionModel<EntityModel<TeamDTOResponse>> collectionModel = super.toCollectionModel(entities);

        // Add links to the collection
        Link selfLink = linkTo(methodOn(TeamControllerImpl.class).getAllTeams())
                .withSelfRel()
                .andAffordances(createCollectionAffordances());

        collectionModel.add(selfLink);
        collectionModel.add(getTemplatedAndPagedLink(linkTo(methodOn(TeamControllerImpl.class).getTeams(null)).toUri().toString()));

        return collectionModel;
    }


    @Override
    public PagedModel<EntityModel<TeamDTOResponse>> toPagedModel(Page<Team> pageTeams) {

        PagedModel<EntityModel<TeamDTOResponse>> pagedModel = super.toPagedModel(pageTeams, TeamDTOResponse.class);
        // Add affordances and links to the paged model
        Link selfLink = linkTo(methodOn(TeamControllerImpl.class).getTeams(null))
                .withSelfRel()
                .andAffordances(createCollectionAffordances());
        pagedModel.add(selfLink);
        pagedModel.add(getTemplatedAndPagedLink(linkTo(methodOn(TeamControllerImpl.class).getTeams(null)).toUri().toString()));
        pagedModel.add(linkTo(methodOn(TeamControllerImpl.class).getAllTeams()).withRel("allTeams"));

        return pagedModel;
    }

    /**
     * Creates affordances for team collections.
     * These affordances represent actions that can be performed on the collection.
     *
     * @return List of affordances available for team collections
     */
    private List<Affordance> createCollectionAffordances() {
        List<Affordance> affordances = new ArrayList<>();
        if (userSecurityService.hasRole(ADMIN_ROLE)) {
            affordances.add(afford(methodOn(TeamControllerImpl.class).createTeam(null)));
            affordances.add(afford(methodOn(TeamControllerImpl.class).createTeam(null)));
        }
        return affordances;
    }
}
