package fr.hoenheimsports.trainingservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.hoenheimsports.trainingservice.assembler.CoachAssembler;
import fr.hoenheimsports.trainingservice.config.TestSecurityConfig;
import fr.hoenheimsports.trainingservice.dto.request.CoachDTORequest;
import fr.hoenheimsports.trainingservice.dto.response.CoachDTOResponse;
import fr.hoenheimsports.trainingservice.mapper.CoachMapper;
import fr.hoenheimsports.trainingservice.model.Coach;
import fr.hoenheimsports.trainingservice.service.CoachService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
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

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CoachController.class)
@Import({TestSecurityConfig.class})
@ActiveProfiles("test")
class CoachControllerImplTest {
    private static final String URI_PATH = "/api/coaches";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CoachService coachService;

    @MockitoBean
    private CoachAssembler coachAssembler;

    @MockitoBean
    private CoachMapper coachMapper;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCreateCoach() throws Exception {
        // Arrange
        CoachDTORequest dtoRequest = getCoachDTORequest();
        CoachDTOResponse dtoResponse = getCoachDTOResponse(1L);

        EntityModel<CoachDTOResponse> entityModel = EntityModel.of(dtoResponse);
        when(coachAssembler.toModel(Mockito.any())).thenReturn(entityModel);
        // Act & Assert

        mockMvc.perform(post(URI_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(dtoResponse.id().intValue())))
                .andExpect(jsonPath("$.name", is(dtoResponse.name())))
                .andExpect(jsonPath("$.surname", is(dtoResponse.surname())))
                .andExpect(jsonPath("$.email", is(dtoResponse.email())))
                .andExpect(jsonPath("$.phone", is(dtoResponse.phone())));

    }



    @Test
    @WithMockUser(username = "admin", roles = {"NO_ADMIN"})
    void testCreateCoach_AccessDenied_WithoutAdminRole() throws Exception {
        // Arrange
        CoachDTORequest dtoRequest = getCoachDTORequest();

        // Act & Assert
        mockMvc.perform(post(URI_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreateCoach_AccessDenied_WithoutAuthentication() throws Exception {
        // Arrange
        CoachDTORequest dtoRequest = getCoachDTORequest();

        // Act & Assert
        mockMvc.perform(post(URI_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetCoachById() throws Exception {
        // Arrange
        long coachId = 1L;
        var coachDTOResponse = getCoachDTOResponse(coachId);
        EntityModel<CoachDTOResponse> coachEntityModel = EntityModel.of(coachDTOResponse);
        when(coachAssembler.toModel(Mockito.any())).thenReturn(coachEntityModel);

        // Act & Assert
        mockMvc.perform(get(URI_PATH + "/" + coachId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) coachId)))
                .andExpect(jsonPath("$.name", is(coachDTOResponse.name())))
                .andExpect(jsonPath("$.surname", is(coachDTOResponse.surname())))
                .andExpect(jsonPath("$.email", is(coachDTOResponse.email())))
                .andExpect(jsonPath("$.phone", is(coachDTOResponse.phone())));
    }



    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUpdateCoach() throws Exception {
        // Arrange
        long coachId = 1L;
        CoachDTORequest dtoRequest = getCoachDTORequest();
        CoachDTOResponse dtoResponse = getCoachDTOResponse(coachId);

        EntityModel<CoachDTOResponse> entityModel = EntityModel.of(dtoResponse);
        when(coachAssembler.toModel(Mockito.any())).thenReturn(entityModel);

        // Act & Assert
        mockMvc.perform(put(URI_PATH + "/" + coachId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) coachId)))
                .andExpect(jsonPath("$.name", is(dtoResponse.name())))
                .andExpect(jsonPath("$.surname", is(dtoResponse.surname())))
                .andExpect(jsonPath("$.email", is(dtoResponse.email())))
                .andExpect(jsonPath("$.phone", is(dtoResponse.phone())));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"NO_ADMIN"})
    void testUpdateCoach_AccessDenied_WithoutAdminRole() throws Exception {
        // Arrange
        long coachId = 1L;
        CoachDTORequest dtoRequest = getCoachDTORequest();
        CoachDTOResponse dtoResponse = getCoachDTOResponse(coachId);

        EntityModel<CoachDTOResponse> entityModel = EntityModel.of(dtoResponse);
        when(coachAssembler.toModel(Mockito.any())).thenReturn(entityModel);

        // Act & Assert
        mockMvc.perform(put(URI_PATH + "/" + coachId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUpdateCoach_AccessDenied_WithoutAuthentication() throws Exception {
        // Arrange
        long coachId = 1L;
        CoachDTORequest dtoRequest = getCoachDTORequest();
        CoachDTOResponse dtoResponse = getCoachDTOResponse(coachId);

        EntityModel<CoachDTOResponse> entityModel = EntityModel.of(dtoResponse);
        when(coachAssembler.toModel(Mockito.any())).thenReturn(entityModel);

        // Act & Assert
        mockMvc.perform(put(URI_PATH + "/" + coachId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoRequest)))
                .andExpect(status().isUnauthorized());
    }
    @Test
    void getCoaches_WithResults() throws Exception {
        // Arrange
        PagedModel<EntityModel<CoachDTOResponse>> pagedCoaches = PagedModel.of(
                List.of(
                        EntityModel.of(getCoachDTOResponse(1L)),
                        EntityModel.of(getCoachDTOResponse(2L))
                ),
                new PagedModel.PageMetadata(10, 0, 2)
        );

        when(coachService.getCoaches(Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(getCoach(1L), getCoach(2L))));
        when(coachAssembler.toPagedModel(Mockito.isNotNull())).thenReturn(pagedCoaches);

        // Act & Assert
        mockMvc.perform(get(URI_PATH)
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.coaches").isArray())
                .andExpect(jsonPath("$._embedded.coaches[0].id", is(1)))
                .andExpect(jsonPath("$._embedded.coaches[0].name", is("John")))
                .andExpect(jsonPath("$._embedded.coaches[1].id", is(2)))
                .andExpect(jsonPath("$._embedded.coaches[1].name", is("John")))
                .andExpect(jsonPath("$.page.size").value(10))
                .andExpect(jsonPath("$.page.totalElements").value(2))
                .andExpect(jsonPath("$.page.totalPages").value(1))
                .andExpect(jsonPath("$.page.number").value(0));
    }

    @Test
    void getCoaches_EmptyResults() throws Exception {
        // Arrange
        PagedModel<EntityModel<CoachDTOResponse>> emptyPagedCoaches = PagedModel.of(
                List.of(),
                new PagedModel.PageMetadata(10, 0, 0)
        );

        when(coachService.getCoaches(Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(getCoach(1L), getCoach(2L))));
        when(coachAssembler.toPagedModel(Mockito.isNotNull())).thenReturn(emptyPagedCoaches);

        // Act & Assert
        mockMvc.perform(get(URI_PATH)
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.halls").doesNotExist())
                .andExpect(jsonPath("$.page.size").value(10))
                .andExpect(jsonPath("$.page.totalElements").value(0))
                .andExpect(jsonPath("$.page.totalPages").value(0))
                .andExpect(jsonPath("$.page.number").value(0));
    }


    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testDeleteCoach() throws Exception {
        // Arrange
        long coachId = 1L;
        Mockito.doNothing().when(coachService).deleteCoach(coachId);

        // Act & Assert
        mockMvc.perform(delete(URI_PATH + "/" + coachId))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"NO_ADMIN"})
    void testDeleteCoach_AccessDenied_WithoutAdminRole() throws Exception {
        // Arrange
        long coachId = 1L;

        // Act & Assert
        mockMvc.perform(delete(URI_PATH + "/" + coachId))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeleteCoach_AccessDenied_WithoutAuthentication() throws Exception {
        // Arrange
        long coachId = 1L;

        // Act & Assert
        mockMvc.perform(delete(URI_PATH + "/" + coachId))
                .andExpect(status().isUnauthorized());
    }


    private static Coach getCoach(long id) {
        return Coach.builder()
                .id(id)
                .name("John")
                .surname("Doe")
                .email("john.doe@example.com")
                .phone("+33123456789")
                .build();
    }
    private static CoachDTORequest getCoachDTORequest() {
        return CoachDTORequest.builder()
                .name("John")
                .surname("Doe")
                .email("john.doe@example.com")
                .phone("+33123456789")
                .build();
    }

    private static CoachDTOResponse getCoachDTOResponse(long id) {
        return CoachDTOResponse.builder()
                .id(id)
                .name("John")
                .surname("Doe")
                .email("john.doe@example.com")
                .phone("+33123456789")
                .build();
    }
}