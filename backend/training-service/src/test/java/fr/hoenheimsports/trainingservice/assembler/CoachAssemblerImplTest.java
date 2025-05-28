package fr.hoenheimsports.trainingservice.assembler;

import fr.hoenheimsports.trainingservice.controller.CoachControllerImpl;
import fr.hoenheimsports.trainingservice.dto.response.CoachDTOResponse;
import fr.hoenheimsports.trainingservice.mapper.CoachMapper;
import fr.hoenheimsports.trainingservice.model.Coach;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
@ExtendWith(MockitoExtension.class)
class CoachAssemblerImplTest {

    @Mock
    private CoachMapper coachMapper;
    @Mock
    private UserSecurityService userSecurityService;
    @Mock
    private PagedResourcesAssembler<Coach> pagedResourcesAssembler;

    @InjectMocks
    private CoachAssemblerImpl coachAssembler;


    @Test
    void toModel_ShouldReturnEntityModelContainingCorrectLinksAndDTO_WhenGivenValidCoachAndUserHasAdminRole() {
        // Arrange

        Coach coach = getCoach();

        CoachDTOResponse coachDTOResponse = getCoachDTOResponse();

        when(coachMapper.toDto(coach)).thenReturn(coachDTOResponse);
        when(userSecurityService.hasRole(CoachAssemblerImpl.ADMIN_ROLE)).thenReturn(true);

        // Act
        EntityModel<CoachDTOResponse> result = coachAssembler.toModel(coach);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo(coachDTOResponse);

        assertLinkPresenceAndHref(result, "self", "/coaches/1");
        assertAffordanceList(result.getLinks("self").getFirst().getAffordances(), 3, "getCoachById", "updateCoach", "deleteCoach", "deleteCoach");

        assertLinkPresenceAndHref(result, "coaches", "/coaches");
        assertAffordanceList(result.getLinks("coaches").getFirst().getAffordances(), 0, "getCoaches");

        verify(coachMapper, times(1)).toDto(coach);
    }



    @Test
    void toModel_ShouldReturnEntityModelContainingCorrectLinksAndDTO_WhenGivenValidCoachAndUserHasNotAdminRole() {
        // Arrange

        Coach coach = getCoach();

        CoachDTOResponse coachDTOResponse = getCoachDTOResponse();

        when(coachMapper.toDto(coach)).thenReturn(coachDTOResponse);
        when(userSecurityService.hasRole(any(String.class))).thenReturn(false);

        // Act
        EntityModel<CoachDTOResponse> result = coachAssembler.toModel(coach);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo(coachDTOResponse);

        assertLinkPresenceAndHref(result, "self", "/coaches/1");
        assertAffordanceList(result.getLinks("self").getFirst().getAffordances(), 0);


        assertLinkPresenceAndHref(result, "coaches", "/coaches");
        assertAffordanceList(result.getLinks("coaches").getFirst().getAffordances(), 0);

        verify(coachMapper, times(1)).toDto(coach);
    }

    @Test
    @SuppressWarnings("ConstantConditions") //NotNull test
    void toModel_ShouldThrowException_WhenCoachIsNull() {
        // Arrange
        Coach nullCoach = null;

        // Act / Assert

        assertThatThrownBy(() -> coachAssembler.toModel(nullCoach))
                .isInstanceOf(IllegalArgumentException.class);
    }


    @Test
    void toPagedModel_ShouldReturnCorrectPagedModel_WhenGivenValidPageAndUserHasAdminRole() {
        // We are testing link and affordance presence/absence as the PagedModel creation is delegated to pagedResourcesAssembler, which we do not control.
        // Arrange
        Page<Coach> pageCoaches = new PageImpl<>(List.of(getCoach()));

        PagedModel<EntityModel<CoachDTOResponse>> pagedModel = PagedModel.of(
                List.of(EntityModel.of(getCoachDTOResponse())),
                new PagedModel.PageMetadata(1, 0, 1)
        );
        pagedModel.add(linkTo(methodOn(CoachControllerImpl.class).getCoaches(null)).withSelfRel());
        when(userSecurityService.hasRole(any(String.class))).thenReturn(true);
        when(pagedResourcesAssembler.toModel(eq(pageCoaches), any(CoachAssemblerImpl.class)))
                .thenReturn(pagedModel);

        // Act
        PagedModel<EntityModel<CoachDTOResponse>> result = coachAssembler.toPagedModel(pageCoaches);

        // Assert
        assertLinkPresenceAndHref(result, "self", "/coaches");
        assertAffordanceList(result.getLinks("self").getFirst().getAffordances(), 2, "createCoach", "createCoach");
    }

    @Test
    void toPagedModel_ShouldReturnCorrectPagedModel_WhenGivenValidPageAndUserHasNotAdminRole() {
        // We are testing link and affordance presence/absence as the PagedModel creation is delegated to pagedResourcesAssembler, which we do not control.
        // Arrange
        Page<Coach> pageCoaches = new PageImpl<>(List.of(getCoach()));

        PagedModel<EntityModel<CoachDTOResponse>> pagedModel = PagedModel.of(
                List.of(EntityModel.of(getCoachDTOResponse())),
                new PagedModel.PageMetadata(1, 0, 1)
        );
        pagedModel.add(linkTo(methodOn(CoachControllerImpl.class).getCoaches(null)).withSelfRel());
        when(userSecurityService.hasRole(any(String.class))).thenReturn(false);
        when(pagedResourcesAssembler.toModel(eq(pageCoaches), any(CoachAssemblerImpl.class)))
                .thenReturn(pagedModel);


        // Act
        PagedModel<EntityModel<CoachDTOResponse>> result = coachAssembler.toPagedModel(pageCoaches);

        // Assert
        assertLinkPresenceAndHref(result, "self", "/coaches");
        assertAffordanceList(result.getLinks("self").getFirst().getAffordances(), 0);
    }

    @Test
    @SuppressWarnings("ConstantConditions") //NotNull test
    void toPagedModel_ShouldThrowException_WhenPageIsNull() {
        // Arrange
        Page<Coach> nullPage = null;

        // Act / Assert
        assertThatThrownBy(() -> coachAssembler.toPagedModel(nullPage))
                .isInstanceOf(IllegalArgumentException.class);
    }



    @SuppressWarnings("ConstantConditions")
    private static void assertAffordanceList(List<Affordance> affordances, int size, String... expectedAffordanceNames) {
        //La création d'un lien ajoute automatique une affordance en HttpMethod GET
        List<String> expectedNamesList  = new ArrayList<>(Arrays.asList( expectedAffordanceNames));
        var affordanceModel = affordances.getFirst().getAffordanceModel(MediaType.parseMediaType("application/prs.hal-forms+json"));
        assertThat(affordanceModel).isNotNull();
        assertThat(affordanceModel.getHttpMethod()).isEqualByComparingTo(HttpMethod.GET);

        expectedNamesList.add(affordances.getFirst().getAffordanceModel(MediaType.parseMediaType("application/prs.hal-forms+json")).getName());
        assertThat(affordances)
                .isNotEmpty()
                .hasSize(size+1)  // La fonction linkTo crée automatiquement une affordance sur son propre lien. Le non-affichage se fait grâce à la distinction des verbes HTTP (GET ne sera pas dans les templates)
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


    private static CoachDTOResponse getCoachDTOResponse() {
        return CoachDTOResponse.builder()
                .id(1L)
                .name("John")
                .surname("Doe")
                .email("john.doe@example.com")
                .phone("+1234567890")
                .build();
    }
    

    private static Coach getCoach() {
        return Coach.builder()
                .id(1L)
                .name("John")
                .surname("Doe")
                .email("john.doe@example.com")
                .phone("+1234567890")
                .build();
    }


}