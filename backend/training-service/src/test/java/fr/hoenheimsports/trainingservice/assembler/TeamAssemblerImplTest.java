package fr.hoenheimsports.trainingservice.assembler;

/* imports */

import fr.hoenheimsports.trainingservice.controller.TeamControllerImpl;
import fr.hoenheimsports.trainingservice.dto.response.TeamDTOResponse;
import fr.hoenheimsports.trainingservice.mapper.TeamMapper;
import fr.hoenheimsports.trainingservice.model.Category;
import fr.hoenheimsports.trainingservice.model.Gender;
import fr.hoenheimsports.trainingservice.model.Team;
import fr.hoenheimsports.trainingservice.model.TrainingSession;
import fr.hoenheimsports.trainingservice.service.UserSecurityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Affordance;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@ExtendWith(MockitoExtension.class)
class TeamAssemblerImplTest {


    @Mock
    private TeamMapper teamMapper;
    @Mock
    private UserSecurityService userSecurityService;
    @Mock
    private PagedResourcesAssembler<Team> pagedResourcesAssembler;
    @Mock
    private RoleCoachAssembler roleCoachAssembler;

    @InjectMocks
    private TeamAssemblerImpl teamAssembler;


    @Test
    void toModel_ShouldReturnEntityModelContainingCorrectLinksAndDTO_WhenGivenValidTeamAndUserHasAdminRole() {
        // Arrange

        Team team = getTeam();

        TeamDTOResponse teamDTOResponse = getTeamDTOResponse();

        when(teamMapper.toDto(team)).thenReturn(teamDTOResponse);
        when(userSecurityService.hasRole(TeamAssemblerImpl.ADMIN_ROLE)).thenReturn(true);

        // Act
        EntityModel<TeamDTOResponse> result = teamAssembler.toModel(team);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotNull();
        assertThat(result.getContent()).usingRecursiveComparison().ignoringFields("trainingSessionDTOResponsesLink", "roleCoaches").isEqualTo(teamDTOResponse);


        assertLinkPresenceAndHref(result, "self", "/teams/1");
        assertAffordanceList(result.getLinks("self").getFirst().getAffordances(), 6, "getTeamById", "updateTeam", "deleteTeam", "deleteTeam");

        assertLinkPresenceAndHref(result, "teams", "/teams");
        assertAffordanceList(result.getLinks("teams").getFirst().getAffordances(), 0, "getTeams");

        verify(teamMapper, times(1)).toDto(team);
    }

    private Team getTeam() {
        return Team.builder()
                .id(1L)
                .teamNumber(1)
                .gender(Gender.M)
                .category(Category.U11)
                .trainingSessions(getTrainingSessionDTOResponses())
                .build();
    }

    private TeamDTOResponse getTeamDTOResponse() {
        return TeamDTOResponse.builder()
                .id(1L)
                .teamNumber(1)
                .gender(Gender.M)
                .category(Category.U11)

                .build();
    }

    private List<TrainingSession> getTrainingSessionDTOResponses() {
        return List.of(
                TrainingSession.builder().id(1L).build(),
                TrainingSession.builder().id(2L).build()
        );
    }


    @Test
    void toModel_ShouldReturnEntityModelContainingCorrectLinksAndDTO_WhenGivenValidTeamAndUserHasNotAdminRole() {
        // Arrange

        Team team = getTeam();

        TeamDTOResponse teamDTOResponse = getTeamDTOResponse();

        when(teamMapper.toDto(team)).thenReturn(teamDTOResponse);
        when(userSecurityService.hasRole(any(String.class))).thenReturn(false);

        // Act
        EntityModel<TeamDTOResponse> result = teamAssembler.toModel(team);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotNull();
        assertThat(result.getContent()).usingRecursiveComparison().ignoringFields("trainingSessionDTOResponsesLink", "roleCoaches").isEqualTo(teamDTOResponse);


        assertLinkPresenceAndHref(result, "self", "/teams/1");
        assertAffordanceList(result.getLinks("self").getFirst().getAffordances(), 0);


        assertLinkPresenceAndHref(result, "teams", "/teams");
        assertAffordanceList(result.getLinks("teams").getFirst().getAffordances(), 0);

        verify(teamMapper, times(1)).toDto(team);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
        //NotNull test
    void toModel_ShouldThrowException_WhenTeamIsNull() {
        // Arrange
        Team nullTeam = null;

        // Act / Assert

        assertThatThrownBy(() -> teamAssembler.toModel(nullTeam))
                .isInstanceOf(IllegalArgumentException.class);
    }


    @Test
    void toPagedModel_ShouldReturnCorrectPagedModel_WhenGivenValidPageAndUserHasAdminRole() {
        // We are testing link and affordance presence/absence as the PagedModel creation is delegated to pagedResourcesAssembler, which we do not control.
        // Arrange
        Page<Team> pageTeames = new PageImpl<>(List.of(getTeam()));

        PagedModel<EntityModel<TeamDTOResponse>> pagedModel = PagedModel.of(
                List.of(EntityModel.of(getTeamDTOResponse())),
                new PagedModel.PageMetadata(1, 0, 1)
        );
        pagedModel.add(linkTo(methodOn(TeamControllerImpl.class).getTeams(null)).withSelfRel());
        when(userSecurityService.hasRole(any(String.class))).thenReturn(true);
        when(pagedResourcesAssembler.toModel(eq(pageTeames), any(TeamAssemblerImpl.class)))
                .thenReturn(pagedModel);

        // Act
        PagedModel<EntityModel<TeamDTOResponse>> result = teamAssembler.toPagedModel(pageTeames);

        // Assert
        assertLinkPresenceAndHref(result, "self", "/teams");
        assertAffordanceList(result.getLinks("self").getFirst().getAffordances(), 2, "createTeam", "createTeam");
    }

    @Test
    void toPagedModel_ShouldReturnCorrectPagedModel_WhenGivenValidPageAndUserHasNotAdminRole() {
        // We are testing link and affordance presence/absence as the PagedModel creation is delegated to pagedResourcesAssembler, which we do not control.
        // Arrange
        Page<Team> pageTeames = new PageImpl<>(List.of(getTeam()));

        PagedModel<EntityModel<TeamDTOResponse>> pagedModel = PagedModel.of(
                List.of(EntityModel.of(getTeamDTOResponse())),
                new PagedModel.PageMetadata(1, 0, 1)
        );
        pagedModel.add(linkTo(methodOn(TeamControllerImpl.class).getTeams(null)).withSelfRel());
        when(userSecurityService.hasRole(any(String.class))).thenReturn(false);
        when(pagedResourcesAssembler.toModel(eq(pageTeames), any(TeamAssemblerImpl.class)))
                .thenReturn(pagedModel);


        // Act
        PagedModel<EntityModel<TeamDTOResponse>> result = teamAssembler.toPagedModel(pageTeames);

        // Assert
        assertLinkPresenceAndHref(result, "self", "/teams");
        assertAffordanceList(result.getLinks("self").getFirst().getAffordances(), 0);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
        //NotNull test
    void toPagedModel_ShouldThrowException_WhenPageIsNull() {
        // Arrange
        Page<Team> nullPage = null;

        // Act / Assert
        assertThatThrownBy(() -> teamAssembler.toPagedModel(nullPage))
                .isInstanceOf(IllegalArgumentException.class);
    }


    @SuppressWarnings("ConstantConditions")
    private static void assertAffordanceList(List<Affordance> affordances, int size, String... expectedAffordanceNames) {
        //La création d'un lien ajoute automatique une affordance en HttpMethod GET
        List<String> expectedNamesList = new ArrayList<>(Arrays.asList(expectedAffordanceNames));
        var affordanceModel = affordances.getFirst().getAffordanceModel(MediaType.parseMediaType("application/prs.hal-forms+json"));
        assertThat(affordanceModel).isNotNull();
        assertThat(affordanceModel.getHttpMethod()).isEqualByComparingTo(HttpMethod.GET);

        expectedNamesList.add(affordances.getFirst().getAffordanceModel(MediaType.parseMediaType("application/prs.hal-forms+json")).getName());
        assertThat(affordances)
                .isNotEmpty()
                .hasSize(size + 1)  // La fonction linkTo crée automatiquement une affordance sur son propre lien. Le non-affichage se fait grâce à la distinction des verbes HTTP (GET ne sera pas dans les templates)
                .allSatisfy(affordance -> assertThat(Optional.ofNullable(affordance.getAffordanceModel(MediaType.parseMediaType("application/prs.hal-forms+json")))).isNotNull())
                .extracting(affordance ->
                        affordance.getAffordanceModel(MediaType.parseMediaType("application/prs.hal-forms+json")).getName()
                )
                .containsAll(expectedNamesList);
    }

    private static void assertLinkPresenceAndHref(RepresentationModel<?> result, String linkName, String path) {
        assertThat(result.hasLink(linkName)).isTrue();
        assertThat(result.getLinks(linkName)).hasSize(1);
        assertThat(result.getLinks(linkName).getFirst().getHref()).contains(path);
    }
}