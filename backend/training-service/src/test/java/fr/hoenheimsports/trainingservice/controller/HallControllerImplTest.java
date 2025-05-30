package fr.hoenheimsports.trainingservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.hoenheimsports.trainingservice.assembler.HallAssembler;
import fr.hoenheimsports.trainingservice.config.TestSecurityConfig;
import fr.hoenheimsports.trainingservice.dto.request.AddressDTORequest;
import fr.hoenheimsports.trainingservice.dto.request.HallDTOCreateRequest;
import fr.hoenheimsports.trainingservice.dto.response.AddressDTOResponse;
import fr.hoenheimsports.trainingservice.dto.response.HallDTOResponse;
import fr.hoenheimsports.trainingservice.mapper.HallMapper;
import fr.hoenheimsports.trainingservice.model.Address;
import fr.hoenheimsports.trainingservice.model.Hall;
import fr.hoenheimsports.trainingservice.service.HallService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = HallController.class)
@Import({TestSecurityConfig.class})
@ActiveProfiles("test")
class HallControllerImplTest {

    private static final String URI_PATH = "/api/halls";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @MockitoBean
    private HallService hallService;
    @MockitoBean
    private HallAssembler hallAssembler;
    @MockitoBean
    private HallMapper hallMapper;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCreateHall() throws Exception {
        //Arrange
        HallDTOCreateRequest hallDTOCreateRequest = getHallDTORequest();
        HallDTOResponse hallDTOResponse = getHallDTOResponse(1L);
        Hall hallRequest = getHall();
        Hall hallResponse = getHall(1L);
        EntityModel<HallDTOResponse> entityModel = EntityModel.of(hallDTOResponse);
        when(hallService.createHall(hallRequest)).thenReturn(hallResponse);
        when(hallAssembler.toModel(hallResponse)).thenReturn(entityModel);
        when(hallMapper.toEntity(hallDTOCreateRequest)).thenReturn(hallRequest);
        //Act et Assert
        mockMvc.perform(post(URI_PATH).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hallDTOCreateRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$.id").value(hallDTOResponse.id()))
                .andExpect(jsonPath("$.name").value(hallDTOResponse.name()))
                .andExpect(jsonPath("$.address.city").value(hallDTOResponse.address().city()))
                .andExpect(jsonPath("$.address.country").value(hallDTOResponse.address().country()))
                .andExpect(jsonPath("$.address.street").value(hallDTOResponse.address().street()))
                .andExpect(jsonPath("$.address.postalCode").value(hallDTOResponse.address().postalCode()));
        verify(hallService, times(1)).createHall(hallRequest);
        verify(hallAssembler, times(1)).toModel(hallResponse);
        verify(hallMapper, times(1)).toEntity(hallDTOCreateRequest);
    }

    @Test// Utilisateur avec un rôle non autorisé
    @WithMockUser(username = "admin", roles = {"NO_ADMIN"})
    void testCreateHall_AccessDenied_WithoutAdminRole() throws Exception {
        // Arrange
        HallDTOCreateRequest hallDTOCreateRequest = getHallDTORequest();

        // Act & Assert
        mockMvc.perform(post(URI_PATH).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hallDTOCreateRequest)))
                .andExpect(status().isForbidden()); // Vérification que l'accès est refusé
    }

    @Test
// Utilisateur avec un rôle non autorisé
    void testCreateHall_AccessDenied_WithoutAuthentication() throws Exception {
        // Arrange
        HallDTOCreateRequest hallDTOCreateRequest = getHallDTORequest();

        // Act & Assert
        mockMvc.perform(post(URI_PATH).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hallDTOCreateRequest)))
                .andExpect(status().isUnauthorized()); // Vérification que l'accès est refusé
    }


    @Test
    void getHallById() throws Exception {
        //Arrange
        long id = 1L;
        Hall hallResponse = getHall(id);
        HallDTOResponse hallDTOResponse = getHallDTOResponse(id);
        when(hallService.getHallById(id)).thenReturn(hallResponse);
        when(hallAssembler.toModel(hallResponse)).thenReturn(EntityModel.of(hallDTOResponse));
        //Act & Assert
        mockMvc.perform(get(URI_PATH + "/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$.id").value(hallDTOResponse.id()))
                .andExpect(jsonPath("$.name").value(hallDTOResponse.name()))
                .andExpect(jsonPath("$.address.city").value(hallDTOResponse.address().city()))
                .andExpect(jsonPath("$.address.country").value(hallDTOResponse.address().country()))
                .andExpect(jsonPath("$.address.street").value(hallDTOResponse.address().street()))
                .andExpect(jsonPath("$.address.postalCode").value(hallDTOResponse.address().postalCode()));
        verify(hallService, times(1)).getHallById(id);
        verify(hallAssembler, times(1)).toModel(hallResponse);
    }


    @Test
    void getHalls_WithResults() throws Exception {
        // Arrange
        List<Hall> hallResponses = List.of(getHall(1L), getHall(2L));
        Page<Hall> hallPage = new PageImpl<>(hallResponses);
        List<HallDTOResponse> hallDTOResponses = List.of(getHallDTOResponse(1L), getHallDTOResponse(2L));


        PagedModel<EntityModel<HallDTOResponse>> model = PagedModel.of(hallDTOResponses.stream()
                .map(EntityModel::of).toList(), new PagedModel.PageMetadata(2, 0, 2));
        when(hallService.getHalls(any(Pageable.class))).thenReturn(hallPage);

        when(hallAssembler.toPagedModel(isNotNull())).thenReturn(model);

        // Act & Assert
        mockMvc.perform(get(URI_PATH))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$.page.size").value(2))
                .andExpect(jsonPath("$.page.totalElements").value(2))
                .andExpect(jsonPath("$.page.totalPages").value(1))
                .andExpect(jsonPath("$.page.number").value(0))
                .andExpect(jsonPath("$._embedded.halls").isArray())
                .andExpect(jsonPath("$._embedded.halls.length()").value(2))
                .andExpect(jsonPath("$._embedded.halls[0].id").value(hallDTOResponses.getFirst().id()))
                .andExpect(jsonPath("$._embedded.halls[1].id").value(hallDTOResponses.getLast().id()));
    }

    @Test
    void getHalls_EmptyResults() throws Exception {
        // Arrange
        when(hallService.getHalls(any(Pageable.class))).thenReturn(Page.empty());
        when(hallAssembler.toPagedModel(isNotNull())).thenReturn(PagedModel.empty(new PagedModel.PageMetadata(0, 0, 0)));

        // Act & Assert
        mockMvc.perform(get(URI_PATH)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$._embedded").doesNotExist())
                .andExpect(jsonPath("$.page.size").value(0))
                .andExpect(jsonPath("$.page.totalElements").value(0))
                .andExpect(jsonPath("$.page.totalPages").value(0))
                .andExpect(jsonPath("$.page.number").value(0));
    }


    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateHall() throws Exception {
        //Arrange
        long id = 1L;
        HallDTOCreateRequest hallDTOCreateRequest = getHallDTORequest();
        Hall hallRequest = getHall();
        Hall hallResponse = getHall(id);
        HallDTOResponse hallDTOResponse = getHallDTOResponse(id);
        when(hallMapper.toEntity(hallDTOCreateRequest)).thenReturn(hallRequest);
        when(hallService.updateHall(id, hallRequest)).thenReturn(hallResponse);
        when(hallAssembler.toModel(hallResponse)).thenReturn(EntityModel.of(hallDTOResponse));
        //Act & Assert
        mockMvc.perform(put(URI_PATH + "/{id}", id).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hallDTOCreateRequest))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$.id").value(hallDTOResponse.id()))
                .andExpect(jsonPath("$.name").value(hallDTOResponse.name()))
                .andExpect(jsonPath("$.address.city").value(hallDTOResponse.address().city()))
                .andExpect(jsonPath("$.address.country").value(hallDTOResponse.address().country()))
                .andExpect(jsonPath("$.address.street").value(hallDTOResponse.address().street()))
                .andExpect(jsonPath("$.address.postalCode").value(hallDTOResponse.address().postalCode()));

        verify(hallMapper, times(1)).toEntity(hallDTOCreateRequest);
        verify(hallService, times(1)).updateHall(id, hallRequest);
        verify(hallAssembler, times(1)).toModel(hallResponse);

    }

    @Test
    @WithMockUser(username = "admin", roles = {"TEST"})
    void updateHall_AccessDenied_WithoutAdminRole() throws Exception {
        // Arrange
        HallDTOCreateRequest hallDTOCreateRequest = getHallDTORequest();
        long id = 1L;
        // Act & Assert
        mockMvc.perform(put(URI_PATH + "/{id}", id).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hallDTOCreateRequest))
                )
                .andExpect(status().isForbidden()); // Vérification que l'accès est refusé
    }

    @Test
// Utilisateur avec un rôle non autorisé
    void updateHall_AccessDenied_WithoutAuthentication() throws Exception {
        // Arrange
        HallDTOCreateRequest hallDTOCreateRequest = getHallDTORequest();
        long id = 1L;
        // Act & Assert
        mockMvc.perform(put(URI_PATH + "/{id}", id).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hallDTOCreateRequest))
                )
                .andExpect(status().isUnauthorized()); // Vérification que l'accès est refusé
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteHall() throws Exception {
        //arrange
        long id = 1L;
        // Act & Assert
        mockMvc.perform(delete(URI_PATH + "/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"TEST"})
    void deleteHall_AccessDenied_WithoutAdminRole() throws Exception {
        //arrange
        long id = 1L;
        // Act & Assert
        mockMvc.perform(delete(URI_PATH + "/{id}", id))
                .andExpect(status().isForbidden());
    }


    @Test
    @WithMockUser(username = "admin", roles = {"TEST"})
    void deleteHall_AccessDenied_WithoutAuthentication() throws Exception {
        //arrange
        long id = 1L;
        // Act & Assert
        mockMvc.perform(delete(URI_PATH + "/{id}", id))
                .andExpect(status().isForbidden());
    }


    private static HallDTOCreateRequest getHallDTORequest() {
        return HallDTOCreateRequest.builder()
                .name("Gymnase")
                .address(
                        AddressDTORequest.builder()
                                .city("Hoenheim")
                                .country("France")
                                .street("rue des vosges")
                                .postalCode("67800")
                                .build())
                .build();
    }

    private static HallDTOResponse getHallDTOResponse(long id) {
        return HallDTOResponse.builder()
                .id(id)
                .name("Gymnase")
                .address(
                        AddressDTOResponse.builder()
                                .city("Hoenheim")
                                .country("France")
                                .street("rue des vosges")
                                .postalCode("67800")
                                .build())
                .build();
    }

    private static Hall getHall() {
        return Hall.builder()
                .name("Gymnase")
                .address(
                        Address.builder()
                                .city("Hoenheim")
                                .country("France")
                                .street("rue des vosges")
                                .postalCode("67800")
                                .build())
                .build();
    }

    private static Hall getHall(long id) {
        return Hall.builder()
                .id(id)
                .name("Gymnase")
                .address(
                        Address.builder()
                                .city("Hoenheim")
                                .country("France")
                                .street("rue des vosges")
                                .postalCode("67800")
                                .build())
                .build();
    }

}