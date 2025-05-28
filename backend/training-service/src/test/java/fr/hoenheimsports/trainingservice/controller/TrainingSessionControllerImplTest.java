package fr.hoenheimsports.trainingservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.hoenheimsports.trainingservice.assembler.TrainingSessionAssembler;
import fr.hoenheimsports.trainingservice.config.TestSecurityConfig;
import fr.hoenheimsports.trainingservice.dto.request.TimeSlotDTORequest;
import fr.hoenheimsports.trainingservice.dto.request.TrainingSessionDTORequest;
import fr.hoenheimsports.trainingservice.dto.response.TimeSlotDTOResponse;
import fr.hoenheimsports.trainingservice.dto.response.TrainingSessionDTOResponse;
import fr.hoenheimsports.trainingservice.mapper.TrainingSessionMapper;
import fr.hoenheimsports.trainingservice.model.TimeSlot;
import fr.hoenheimsports.trainingservice.model.TrainingSession;
import fr.hoenheimsports.trainingservice.service.TrainingSessionService;
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

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TrainingSessionControllerImpl.class)
@Import({TestSecurityConfig.class})
@ActiveProfiles("test")
class TrainingSessionControllerImplTest {

    private static final String URI_PATH = "/api/training-sessions";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TrainingSessionService trainingSessionService;

    @MockitoBean
    private TrainingSessionAssembler trainingSessionAssembler;

    @MockitoBean
    private TrainingSessionMapper trainingSessionMapper;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCreateTrainingSession() throws Exception {
        // Préparation des données
        TrainingSessionDTORequest trainingSessionDTORequest = getTrainingSessionDTORequest();
        TrainingSession trainingSession = getTrainingSession();
        TrainingSessionDTOResponse trainingSessionDTOResponse = getTrainingSessionDTOResponse(1L);
        EntityModel<TrainingSessionDTOResponse> trainingSessionModel = EntityModel.of(trainingSessionDTOResponse);

        when(trainingSessionMapper.toEntity(trainingSessionDTORequest)).thenReturn(trainingSession);
        when(trainingSessionService.createTrainingSession(trainingSession)).thenReturn(trainingSession);
        when(trainingSessionAssembler.toModel(any())).thenReturn(trainingSessionModel);

        // Appel à l'API
        mockMvc.perform(post(URI_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainingSessionDTORequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$.id").value(trainingSessionDTOResponse.id()))
                .andExpect(jsonPath("$.timeSlot.endTime").value("12:00:00"))
                .andExpect(jsonPath("$.timeSlot.startTime").value("10:00:00"))
                .andExpect(jsonPath("$.timeSlot.dayOfWeek").value(trainingSessionDTOResponse.timeSlot().dayOfWeek().toString()));


        // Vérification des interactions
        verify(trainingSessionMapper).toEntity(trainingSessionDTORequest);
        verify(trainingSessionService).createTrainingSession(trainingSession);
    }

    @Test
    @WithMockUser(username = "user", roles = {"NO_ADMIN"})
    void testCreateTrainingSession_AccessDenied_WithoutAdminRole() throws Exception {
        var trainingSessionDTORequest = getTrainingSessionDTORequest();

        mockMvc.perform(post(URI_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainingSessionDTORequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreateTrainingSession_AccessDenied_WithoutAuthentication() throws Exception {
        var trainingSessionDTORequest = getTrainingSessionDTORequest();

        mockMvc.perform(post(URI_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainingSessionDTORequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetTrainingSessionById() throws Exception {
        long trainingSessionId = 1L;
        TrainingSessionDTOResponse trainingSessionDTOResponse = getTrainingSessionDTOResponse(trainingSessionId);
        EntityModel<TrainingSessionDTOResponse> trainingSessionModel = EntityModel.of(trainingSessionDTOResponse);

        when(trainingSessionService.getTrainingSessionById(trainingSessionId)).thenReturn(getTrainingSession(trainingSessionId));
        when(trainingSessionAssembler.toModel(any())).thenReturn(trainingSessionModel);

        mockMvc.perform(get(URI_PATH + "/" + trainingSessionId))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$.id").value(trainingSessionDTOResponse.id()))
                .andExpect(jsonPath("$.timeSlot.endTime").value("12:00:00"))
                .andExpect(jsonPath("$.timeSlot.startTime").value("10:00:00"))
                .andExpect(jsonPath("$.timeSlot.dayOfWeek").value(trainingSessionDTOResponse.timeSlot().dayOfWeek().toString()));

        verify(trainingSessionService).getTrainingSessionById(trainingSessionId);
        verify(trainingSessionAssembler).toModel(any());
    }

    @Test
    void getTrainingSessions_WithResults() throws Exception {

        List<TrainingSession> trainingSessions = List.of(getTrainingSession(1L), getTrainingSession(2L));
        Page<TrainingSession> hallPage = new PageImpl<>(trainingSessions);
        List<TrainingSessionDTOResponse> trainingSessionDTOResponses = List.of(getTrainingSessionDTOResponse(1L), getTrainingSessionDTOResponse(2L));

        PagedModel<EntityModel<TrainingSessionDTOResponse>> model =  PagedModel.of(trainingSessionDTOResponses.stream()
                .map(EntityModel::of).toList(), new PagedModel.PageMetadata(2, 0, 2));

        when(trainingSessionService.getTrainingSessions(any(Pageable.class))).thenReturn(hallPage);
        when(trainingSessionAssembler.toPagedModel(isNotNull())).thenReturn(model);

        mockMvc.perform(get(URI_PATH))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$._embedded.trainingSessions").isArray())
                .andExpect(jsonPath("$._embedded.trainingSessions.length()").value(2))
                .andExpect(jsonPath("$._embedded.trainingSessions[0].id").value(1L))
                .andExpect(jsonPath("$._embedded.trainingSessions[1].id").value(2L))
                .andExpect(jsonPath("$.page.totalElements").value(2));

        verify(trainingSessionService).getTrainingSessions(any());
        verify(trainingSessionAssembler).toPagedModel(any());
    }

    @Test
    void getTrainingSessions_EmptyResults() throws Exception {
        when(trainingSessionService.getTrainingSessions(any())).thenReturn(Page.empty());
        when(trainingSessionAssembler.toPagedModel(any())).thenReturn(PagedModel.empty(new PagedModel.PageMetadata(0, 0, 0)));

        mockMvc.perform(get(URI_PATH)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").doesNotExist())
                .andExpect(jsonPath("$.page.totalElements").value(0));

        verify(trainingSessionService).getTrainingSessions(any());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUpdateTrainingSession() throws Exception {
        // Préparation des données
        long trainingSessionId = 1L;
        var trainingSessionDTOUpdateRequest = getTrainingSessionDTORequest();
        TrainingSession trainingSession = getTrainingSession(trainingSessionId);
        TrainingSessionDTOResponse trainingSessionDTOResponse = getTrainingSessionDTOResponse(trainingSessionId);
        EntityModel<TrainingSessionDTOResponse> trainingSessionModel = EntityModel.of(trainingSessionDTOResponse);

        when(trainingSessionMapper.toEntity(trainingSessionDTOUpdateRequest)).thenReturn(trainingSession);
        when(trainingSessionService.updateTrainingSession(trainingSessionId, trainingSession)).thenReturn(trainingSession);
        when(trainingSessionAssembler.toModel(trainingSession)).thenReturn(trainingSessionModel);

        // Appel à l'API
        mockMvc.perform(put(URI_PATH + "/" + trainingSessionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainingSessionDTOUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(trainingSessionDTOResponse.id()))
                .andExpect(jsonPath("$.timeSlot.endTime").value("12:00:00"))
                .andExpect(jsonPath("$.timeSlot.startTime").value("10:00:00"))
                .andExpect(jsonPath("$.timeSlot.dayOfWeek").value(trainingSessionDTOResponse.timeSlot().dayOfWeek().toString()));


        // Vérification des interactions
        verify(trainingSessionMapper).toEntity(trainingSessionDTOUpdateRequest);
        verify(trainingSessionService).updateTrainingSession(trainingSessionId, trainingSession);
    }

    @Test
    @WithMockUser(username = "user", roles = {"NO_ADMIN"})
    void testUpdateTrainingSession_AccessDenied_WithoutAdminRole() throws Exception {
        // Préparation des données
        long trainingSessionId = 1L;
        var trainingSessionDTOUpdateRequest = getTrainingSessionDTORequest();

        // Appel à l'API
        mockMvc.perform(put(URI_PATH + "/" + trainingSessionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainingSessionDTOUpdateRequest)))
                .andExpect(status().isForbidden());

        // Vérification : pas d'interaction avec le service
        verify(trainingSessionService, never()).updateTrainingSession(anyLong(), any());
    }

    @Test
    void testUpdateTrainingSession_AccessDenied_WithoutAuthentication() throws Exception {
        // Préparation des données
        long trainingSessionId = 1L;
        var trainingSessionDTOUpdateRequest = getTrainingSessionDTORequest();

        // Appel à l'API sans authentification
        mockMvc.perform(put(URI_PATH + "/" + trainingSessionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainingSessionDTOUpdateRequest)))
                .andExpect(status().isUnauthorized());

        // Vérification : pas d'interaction avec le service
        verify(trainingSessionService, never()).updateTrainingSession(anyLong(), any());
    }


    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteTrainingSession() throws Exception {
        long trainingSessionId = 1L;

        mockMvc.perform(delete(URI_PATH + "/" + trainingSessionId))
                .andExpect(status().isNoContent());

        verify(trainingSessionService).deleteTrainingSession(trainingSessionId);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"NO_ADMIN"})
    void deleteTrainingSession_AccessDenied_WithoutAdminRole() throws Exception {
        long trainingSessionId = 1L;

        mockMvc.perform(delete(URI_PATH + "/" + trainingSessionId))
                .andExpect(status().isForbidden());
    }

    private static TrainingSessionDTORequest getTrainingSessionDTORequest() {
        return TrainingSessionDTORequest.builder()
                .timeSlot(getTimeSlotDTORequest())
                .build();
    }

    private static TimeSlotDTORequest getTimeSlotDTORequest() {
        return TimeSlotDTORequest.builder()
                .endTime(LocalTime.of(12, 0,0))
                .startTime(LocalTime.of(10, 0,0))
                .dayOfWeek(DayOfWeek.FRIDAY)
                .build();
    }

    private static TrainingSessionDTOResponse getTrainingSessionDTOResponse(long id) {
        return TrainingSessionDTOResponse.builder()
                .id(id)
                .timeSlot(getTimeSlotDTOResponse())
                .build();
    }

    private static TimeSlotDTOResponse getTimeSlotDTOResponse() {
        return TimeSlotDTOResponse.builder()
                .endTime(LocalTime.of(12, 0,0))
                .startTime(LocalTime.of(10, 0,0))
                .dayOfWeek(DayOfWeek.FRIDAY)
                .build();
    }



    private static TrainingSession getTrainingSession() {
        return TrainingSession.builder()
                .timeSlot(getTimeSlot())
                .build();
    }

    private static TimeSlot getTimeSlot() {
        return TimeSlot.builder()
                .endTime(LocalTime.of(12, 0,0))
                .startTime(LocalTime.of(10, 0,0))
                .dayOfWeek(DayOfWeek.FRIDAY)
                .build();
    }

    private static TrainingSession getTrainingSession(long id) {
        return TrainingSession.builder()
                .id(id)
                .timeSlot(getTimeSlot())
                .build();
    }
}