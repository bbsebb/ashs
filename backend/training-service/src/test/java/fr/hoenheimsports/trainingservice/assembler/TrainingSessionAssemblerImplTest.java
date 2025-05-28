package fr.hoenheimsports.trainingservice.assembler;

import fr.hoenheimsports.trainingservice.controller.TrainingSessionControllerImpl;
import fr.hoenheimsports.trainingservice.dto.response.HallDTOResponse;
import fr.hoenheimsports.trainingservice.dto.response.TimeSlotDTOResponse;
import fr.hoenheimsports.trainingservice.dto.response.TrainingSessionDTOResponse;
import fr.hoenheimsports.trainingservice.mapper.TrainingSessionMapper;
import fr.hoenheimsports.trainingservice.model.TimeSlot;
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

import java.time.DayOfWeek;
import java.time.LocalTime;
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
class TrainingSessionAssemblerImplTest {


    @Mock
    private TrainingSessionMapper trainingSessionMapper;
    @Mock
    private UserSecurityService userSecurityService;
    @Mock
    private PagedResourcesAssembler<TrainingSession> pagedResourcesAssembler;
    @Mock
    private HallAssembler hallAssembler;

    @InjectMocks
    private TrainingSessionAssemblerImpl trainingSessionAssembler;


    @Test
    void toModel_ShouldReturnEntityModelContainingCorrectLinksAndDTO_WhenGivenValidTrainingSessionAndUserHasAdminRole() {
        // Arrange

        TrainingSession trainingSession = getTrainingSession();

        TrainingSessionDTOResponse trainingSessionDTOResponse = getTrainingSessionDTOResponse();

        when(trainingSessionMapper.toDto(trainingSession)).thenReturn(trainingSessionDTOResponse);
        when(userSecurityService.hasRole(TrainingSessionAssemblerImpl.ADMIN_ROLE)).thenReturn(true);
        when(hallAssembler.toModel(trainingSession.getHall())).thenReturn(
                EntityModel.of(HallDTOResponse.builder().build())
        );

        // Act
        EntityModel<TrainingSessionDTOResponse> result = trainingSessionAssembler.toModel(trainingSession);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).usingRecursiveComparison().ignoringFields("hall").isEqualTo(trainingSessionDTOResponse);

        assertLinkPresenceAndHref(result, "self", "/training-sessions/1");
        assertAffordanceList(result.getLinks("self").getFirst().getAffordances(), 3, "getTrainingSessionById", "updateTrainingSession", "deleteTrainingSession", "deleteTrainingSession");

        assertLinkPresenceAndHref(result, "trainingSessions", "/training-sessions");
        assertAffordanceList(result.getLinks("trainingSessions").getFirst().getAffordances(), 0, "getTrainingSessions");

        verify(trainingSessionMapper, times(1)).toDto(trainingSession);
    }

    private TrainingSession getTrainingSession() {
        return TrainingSession.builder()
                .id(1L)
                .timeSlot(getTimeSlot())
                .build();
    }

    private TimeSlot getTimeSlot() {
        return TimeSlot.builder()
                .dayOfWeek(DayOfWeek.FRIDAY)
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(12, 0))
                .build();
    }

    private TrainingSessionDTOResponse getTrainingSessionDTOResponse() {
        return TrainingSessionDTOResponse.builder()
                .id(1L)
                .timeSlot(getTimeSlotDTOResponse())
                .build();
    }

    private TimeSlotDTOResponse getTimeSlotDTOResponse() {
        return TimeSlotDTOResponse.builder()
                .dayOfWeek(DayOfWeek.FRIDAY)
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(12, 0))
                .build();
    }


    @Test
    void toModel_ShouldReturnEntityModelContainingCorrectLinksAndDTO_WhenGivenValidTrainingSessionAndUserHasNotAdminRole() {
        // Arrange

        TrainingSession trainingSession = getTrainingSession();

        TrainingSessionDTOResponse trainingSessionDTOResponse = getTrainingSessionDTOResponse();

        when(trainingSessionMapper.toDto(trainingSession)).thenReturn(trainingSessionDTOResponse);
        when(userSecurityService.hasRole(any(String.class))).thenReturn(false);
        when(hallAssembler.toModel(trainingSession.getHall())).thenReturn(
                EntityModel.of(HallDTOResponse.builder().build())
        );
        // Act
        EntityModel<TrainingSessionDTOResponse> result = trainingSessionAssembler.toModel(trainingSession);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).usingRecursiveComparison().ignoringFields("hall").isEqualTo(trainingSessionDTOResponse);

        assertLinkPresenceAndHref(result, "self", "/training-sessions/1");
        assertAffordanceList(result.getLinks("self").getFirst().getAffordances(), 0);


        assertLinkPresenceAndHref(result, "trainingSessions", "/training-sessions");
        assertAffordanceList(result.getLinks("trainingSessions").getFirst().getAffordances(), 0);

        verify(trainingSessionMapper, times(1)).toDto(trainingSession);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
        //NotNull test
    void toModel_ShouldThrowException_WhenTrainingSessionIsNull() {
        // Arrange
        TrainingSession nullTrainingSession = null;

        // Act / Assert

        assertThatThrownBy(() -> trainingSessionAssembler.toModel(nullTrainingSession))
                .isInstanceOf(IllegalArgumentException.class);
    }


    @Test
    void toPagedModel_ShouldReturnCorrectPagedModel_WhenGivenValidPageAndUserHasAdminRole() {
        // We are testing link and affordance presence/absence as the PagedModel creation is delegated to pagedResourcesAssembler, which we do not control.
        // Arrange
        Page<TrainingSession> pageTrainingSessiones = new PageImpl<>(List.of(getTrainingSession()));

        PagedModel<EntityModel<TrainingSessionDTOResponse>> pagedModel = PagedModel.of(
                List.of(EntityModel.of(getTrainingSessionDTOResponse())),
                new PagedModel.PageMetadata(1, 0, 1)
        );
        pagedModel.add(linkTo(methodOn(TrainingSessionControllerImpl.class).getTrainingSessions(null)).withSelfRel());
        when(userSecurityService.hasRole(any(String.class))).thenReturn(true);
        when(pagedResourcesAssembler.toModel(eq(pageTrainingSessiones), any(TrainingSessionAssemblerImpl.class)))
                .thenReturn(pagedModel);

        // Act
        PagedModel<EntityModel<TrainingSessionDTOResponse>> result = trainingSessionAssembler.toPagedModel(pageTrainingSessiones);

        // Assert
        assertLinkPresenceAndHref(result, "self", "/training-sessions");
        assertAffordanceList(result.getLinks("self").getFirst().getAffordances(), 2, "createTrainingSession", "createTrainingSession");
    }

    @Test
    void toPagedModel_ShouldReturnCorrectPagedModel_WhenGivenValidPageAndUserHasNotAdminRole() {
        // We are testing link and affordance presence/absence as the PagedModel creation is delegated to pagedResourcesAssembler, which we do not control.
        // Arrange
        Page<TrainingSession> pageTrainingSessiones = new PageImpl<>(List.of(getTrainingSession()));

        PagedModel<EntityModel<TrainingSessionDTOResponse>> pagedModel = PagedModel.of(
                List.of(EntityModel.of(getTrainingSessionDTOResponse())),
                new PagedModel.PageMetadata(1, 0, 1)
        );
        pagedModel.add(linkTo(methodOn(TrainingSessionControllerImpl.class).getTrainingSessions(null)).withSelfRel());
        when(userSecurityService.hasRole(any(String.class))).thenReturn(false);
        when(pagedResourcesAssembler.toModel(eq(pageTrainingSessiones), any(TrainingSessionAssemblerImpl.class)))
                .thenReturn(pagedModel);


        // Act
        PagedModel<EntityModel<TrainingSessionDTOResponse>> result = trainingSessionAssembler.toPagedModel(pageTrainingSessiones);

        // Assert
        assertLinkPresenceAndHref(result, "self", "/training-sessions");
        assertAffordanceList(result.getLinks("self").getFirst().getAffordances(), 0);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
        //NotNull test
    void toPagedModel_ShouldThrowException_WhenPageIsNull() {
        // Arrange
        Page<TrainingSession> nullPage = null;

        // Act / Assert
        assertThatThrownBy(() -> trainingSessionAssembler.toPagedModel(nullPage))
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