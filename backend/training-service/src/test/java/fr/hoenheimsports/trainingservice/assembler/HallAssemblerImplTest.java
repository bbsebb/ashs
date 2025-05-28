package fr.hoenheimsports.trainingservice.assembler;

import fr.hoenheimsports.trainingservice.controller.HallControllerImpl;
import fr.hoenheimsports.trainingservice.dto.response.AddressDTOResponse;
import fr.hoenheimsports.trainingservice.dto.response.HallDTOResponse;
import fr.hoenheimsports.trainingservice.mapper.HallMapper;
import fr.hoenheimsports.trainingservice.model.Address;
import fr.hoenheimsports.trainingservice.model.Hall;
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
class HallAssemblerImplTest {

    @Mock
    private HallMapper hallMapper;
    @Mock
    private UserSecurityService userSecurityService;
    @Mock
    private PagedResourcesAssembler<Hall> pagedResourcesAssembler;

    @InjectMocks
    private HallAssemblerImpl hallAssembler;
    
    

    @Test
    void toModel_ShouldReturnEntityModelContainingCorrectLinksAndDTO_WhenGivenValidHallAndUserHasAdminRole() {
        // Arrange

        Hall hall = getHall();

        HallDTOResponse hallDTOResponse = getHallDTOResponse();

        when(hallMapper.toDto(hall)).thenReturn(hallDTOResponse);
        when(userSecurityService.hasRole(HallAssemblerImpl.ADMIN_ROLE)).thenReturn(true);

        // Act
        EntityModel<HallDTOResponse> result = hallAssembler.toModel(hall);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo(hallDTOResponse);

        assertLinkPresenceAndHref(result, "self", "/halls/1");
        assertAffordanceList(result.getLinks("self").getFirst().getAffordances(),3,"getHallById","updateHall","deleteHall","deleteHall");


        assertLinkPresenceAndHref(result, "halls", "/halls");
        assertAffordanceList(result.getLinks("halls").getFirst().getAffordances(),0,"getHalls");

        verify(hallMapper, times(1)).toDto(hall);
    }



    @Test
    void toModel_ShouldReturnEntityModelContainingCorrectLinksAndDTO_WhenGivenValidHallAndUserHasNotAdminRole() {
        // Arrange

        Hall hall = getHall();

        HallDTOResponse hallDTOResponse = getHallDTOResponse();

        when(hallMapper.toDto(hall)).thenReturn(hallDTOResponse);
        when(userSecurityService.hasRole(any(String.class))).thenReturn(false);

        // Act
        EntityModel<HallDTOResponse> result = hallAssembler.toModel(hall);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo(hallDTOResponse);

        assertLinkPresenceAndHref(result, "self", "/halls/1");
        assertAffordanceList(result.getLinks("self").getFirst().getAffordances(),0);


        assertLinkPresenceAndHref(result, "halls", "/halls");
        assertAffordanceList(result.getLinks("halls").getFirst().getAffordances(),0);

        verify(hallMapper, times(1)).toDto(hall);
    }

    @Test
    @SuppressWarnings("ConstantConditions") //NotNull test
    void toModel_ShouldThrowException_WhenHallIsNull() {
        // Arrange
        Hall nullHall = null;

        // Act / Assert

        assertThatThrownBy(() -> hallAssembler.toModel(nullHall))
                .isInstanceOf(IllegalArgumentException.class);
    }


    @Test
    void toPagedModel_ShouldReturnCorrectPagedModel_WhenGivenValidPageAndUserHasAdminRole() {
        // We are testing link and affordance presence/absence as the PagedModel creation is delegated to pagedResourcesAssembler, which we do not control.
        // Arrange
        Page<Hall> pageHalls = new PageImpl<>(List.of(getHall()));

        PagedModel<EntityModel<HallDTOResponse>> pagedModel = PagedModel.of(
                List.of(EntityModel.of(getHallDTOResponse())),
                new PagedModel.PageMetadata(1, 0, 1)
        );
        pagedModel.add(linkTo(methodOn(HallControllerImpl.class).getHalls(null)).withSelfRel());
        when(userSecurityService.hasRole(any(String.class))).thenReturn(true);
        when(pagedResourcesAssembler.toModel(eq(pageHalls), any(HallAssemblerImpl.class)))
                .thenReturn(pagedModel);

        // Act
        PagedModel<EntityModel<HallDTOResponse>> result = hallAssembler.toPagedModel(pageHalls);

        // Assert
        assertLinkPresenceAndHref(result, "self", "/halls");
        assertAffordanceList(result.getLinks("self").getFirst().getAffordances(),2,"createHall","createHall");
    }

    @Test
    void toPagedModel_ShouldReturnCorrectPagedModel_WhenGivenValidPageAndUserHasNotAdminRole() {
        // We are testing link and affordance presence/absence as the PagedModel creation is delegated to pagedResourcesAssembler, which we do not control.
        // Arrange
        Page<Hall> pageHalls = new PageImpl<>(List.of(getHall()));

        PagedModel<EntityModel<HallDTOResponse>> pagedModel = PagedModel.of(
                List.of(EntityModel.of(getHallDTOResponse())),
                new PagedModel.PageMetadata(1, 0, 1)
        );
        pagedModel.add(linkTo(methodOn(HallControllerImpl.class).getHalls(null)).withSelfRel());
        when(userSecurityService.hasRole(any(String.class))).thenReturn(false);
        when(pagedResourcesAssembler.toModel(eq(pageHalls), any(HallAssemblerImpl.class)))
                .thenReturn(pagedModel);


        // Act
        PagedModel<EntityModel<HallDTOResponse>> result = hallAssembler.toPagedModel(pageHalls);

        // Assert
        assertLinkPresenceAndHref(result, "self", "/halls");
        assertAffordanceList(result.getLinks("self").getFirst().getAffordances(),0);
    }

    @Test
    @SuppressWarnings("ConstantConditions") //NotNull test
    void toPagedModel_ShouldThrowException_WhenPageIsNull() {
        // Arrange
        Page<Hall> nullPage = null;

        // Act / Assert
        assertThatThrownBy(() -> hallAssembler.toPagedModel(nullPage))
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

    private static HallDTOResponse getHallDTOResponse() {
        return HallDTOResponse.builder()
                .id(1L)
                .name("Sports Hall 1")
                .address(getAddressDTOResponse())
                .build();
    }

    private static AddressDTOResponse getAddressDTOResponse() {
        return AddressDTOResponse.builder()
                .street("123 Main Street")
                .city("Springfield")
                .postalCode("12345")
                .country("Country")
                .build();
    }

    private static Hall getHall() {
        return Hall.builder()
                .id(1L)
                .name("Sports Hall 1")
                .address(getAddress())
                .build();
    }

    private static Address getAddress() {
        return Address.builder()
                .street("123 Main Street")
                .city("Springfield")
                .postalCode("12345")
                .country("Country")
                .build();
    }

}