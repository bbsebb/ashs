package fr.hoenheimsports.trainingservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.hoenheimsports.trainingservice.assembler.TeamAssembler;
import fr.hoenheimsports.trainingservice.config.TestSecurityConfig;
import fr.hoenheimsports.trainingservice.dto.request.*;
import fr.hoenheimsports.trainingservice.dto.response.TeamDTOResponse;
import fr.hoenheimsports.trainingservice.mapper.TeamMapper;
import fr.hoenheimsports.trainingservice.mapper.TrainingSessionMapper;
import fr.hoenheimsports.trainingservice.model.*;
import fr.hoenheimsports.trainingservice.service.TeamService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TeamControllerImpl.class)
@Import({TestSecurityConfig.class})
@ActiveProfiles("test")
class TeamControllerImplTest {

    private static final String URI_PATH = "/api/teams";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TeamService teamService;

    @MockitoBean
    private TeamAssembler teamAssembler;

    @MockitoBean
    private TeamMapper teamMapper;
    @MockitoBean
    private TrainingSessionMapper trainingSessionMapper;
    @Mock
    Team team;
    @Mock
    EntityModel<TeamDTOResponse> teamModel;

    @Mock
    Page<Team> hallPage;




    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCreateTeam() throws Exception {
        // Préparation des données
        TeamDTOCreateRequest teamDTORequest = getTeamDTOCreateRequest();



        when(teamMapper.toEntity(teamDTORequest)).thenReturn(team);
        when(teamService.createTeam(team)).thenReturn(team);
        when(teamAssembler.toModel(team)).thenReturn(teamModel);

        // Appel à l'API
        mockMvc.perform(post(URI_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamDTORequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/hal+json"));

        // Vérification des interactions
        verify(teamMapper).toEntity(teamDTORequest);
        verify(teamService).createTeam(team);
        verify(teamAssembler).toModel(team);
    }

    @Test
    @WithMockUser(username = "user", roles = {"NO_ADMIN"})
    void testCreateTeam_AccessDenied_WithoutAdminRole() throws Exception {
        TeamDTOCreateRequest teamDTORequest = getTeamDTOCreateRequest();

        mockMvc.perform(post(URI_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamDTORequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreateTeam_AccessDenied_WithoutAuthentication() throws Exception {
        TeamDTOCreateRequest teamDTORequest = getTeamDTOCreateRequest();

        mockMvc.perform(post(URI_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamDTORequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetTeamById() throws Exception {
        long teamId = 1L;

        when(teamService.getTeamById(teamId)).thenReturn(team);
        when(teamAssembler.toModel(team)).thenReturn(teamModel);

        mockMvc.perform(get(URI_PATH + "/" + teamId))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"));

        verify(teamService).getTeamById(teamId);
        verify(teamAssembler).toModel(team);
    }

    @Test
    void getTeams_WithResults() throws Exception {


        List<TeamDTOResponse> teamDTOResponses = List.of(getTeamDTOResponse(1L), getTeamDTOResponse(2L));

        PagedModel<EntityModel<TeamDTOResponse>> model =  PagedModel.of(teamDTOResponses.stream()
                .map(EntityModel::of).toList(), new PagedModel.PageMetadata(2, 0, 2));

        when(teamService.getTeams(any(Pageable.class))).thenReturn(hallPage);
        when(teamAssembler.toPagedModel(hallPage)).thenReturn(model);

        mockMvc.perform(get(URI_PATH))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"));

        verify(teamService).getTeams(any(Pageable.class));
        verify(teamAssembler).toPagedModel(hallPage);
    }

    @Test
    void getTeams_EmptyResults() throws Exception {
        when(teamService.getTeams(any())).thenReturn(Page.empty());
        when(teamAssembler.toPagedModel(any())).thenReturn(PagedModel.empty(new PagedModel.PageMetadata(0, 0, 0)));

        mockMvc.perform(get(URI_PATH)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").doesNotExist())
                .andExpect(jsonPath("$.page.totalElements").value(0));

        verify(teamService).getTeams(any());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUpdateTeam() throws Exception {
        // Préparation des données
        long teamId = 1L;
        TeamDTOUpdateRequest teamDTOUpdateRequest = getTeamDTOUpdateRequest();
        Team team = getTeam(teamId);
        TeamDTOResponse teamDTOResponse = getTeamDTOResponse(teamId);
        EntityModel<TeamDTOResponse> teamModel = EntityModel.of(teamDTOResponse);

        when(teamMapper.toEntity(teamDTOUpdateRequest)).thenReturn(team);
        when(teamService.updateTeam(teamId, team)).thenReturn(team);
        when(teamAssembler.toModel(team)).thenReturn(teamModel);

        // Appel à l'API
        mockMvc.perform(put(URI_PATH + "/" + teamId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamDTOUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(teamDTOResponse.id()))
                .andExpect(jsonPath("$.category").value(teamDTOResponse.category().toString()))
                .andExpect(jsonPath("$.gender").value(teamDTOResponse.gender().toString()))
                .andExpect(jsonPath("$.teamNumber").value(teamDTOResponse.teamNumber()));


        // Vérification des interactions
        verify(teamMapper).toEntity(teamDTOUpdateRequest);
        verify(teamService).updateTeam(teamId, team);
    }

    @Test
    @WithMockUser(username = "user", roles = {"NO_ADMIN"})
    void testUpdateTeam_AccessDenied_WithoutAdminRole() throws Exception {
        // Préparation des données
        long teamId = 1L;
        TeamDTOUpdateRequest teamDTOUpdateRequest = getTeamDTOUpdateRequest();

        // Appel à l'API
        mockMvc.perform(put(URI_PATH + "/" + teamId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamDTOUpdateRequest)))
                .andExpect(status().isForbidden());

        // Vérification : pas d'interaction avec le service
        verify(teamService, never()).updateTeam(anyLong(), any());
    }

    @Test
    void testUpdateTeam_AccessDenied_WithoutAuthentication() throws Exception {
        // Préparation des données
        long teamId = 1L;
        TeamDTOUpdateRequest teamDTOUpdateRequest = getTeamDTOUpdateRequest();

        // Appel à l'API sans authentification
        mockMvc.perform(put(URI_PATH + "/" + teamId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamDTOUpdateRequest)))
                .andExpect(status().isUnauthorized());

        // Vérification : pas d'interaction avec le service
        verify(teamService, never()).updateTeam(anyLong(), any());
    }


    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteTeam() throws Exception {
        long teamId = 1L;

        mockMvc.perform(delete(URI_PATH + "/" + teamId))
                .andExpect(status().isNoContent());

        verify(teamService).deleteTeam(teamId);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"NO_ADMIN"})
    void deleteTeam_AccessDenied_WithoutAdminRole() throws Exception {
        long teamId = 1L;

        mockMvc.perform(delete(URI_PATH + "/" + teamId))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    //TODO checker les objets imbriqués trainingSession
    void testAddTrainingSession() throws Exception {
        // Préparation des données
        long teamId = 1L;
        long hallId = 1L;
        TeamDTOCreateRequest teamDTORequest = getTeamDTOCreateRequest();
        Team team = getTeam();
        AddTrainingSessionInTeamDTORequest addTrainingSessionInTeamDTORequest = new AddTrainingSessionInTeamDTORequest(hallId,new TrainingSessionDTORequest(new TimeSlotDTORequest(DayOfWeek.FRIDAY, LocalTime.of(10,0), LocalTime.of(11,0))));
        TeamDTOResponse teamDTOResponse = getTeamDTOResponse(teamId);
        EntityModel<TeamDTOResponse> teamModel = EntityModel.of(teamDTOResponse);
        TrainingSession trainingSession = TrainingSession.builder()
                .timeSlot(TimeSlot.builder()
                        .dayOfWeek(DayOfWeek.FRIDAY)
                        .startTime(LocalTime.of(10,0))
                        .endTime(LocalTime.of(11,0))
                        .build())
                .build();
        Team updatedTeam = getTeam(teamId);
        when(trainingSessionMapper.toEntity(addTrainingSessionInTeamDTORequest.trainingSessionDTORequest())).thenReturn(trainingSession);
        when(teamService.addTrainingSession(teamId,hallId,trainingSession)).thenReturn(updatedTeam);
        when(teamAssembler.toModel(updatedTeam)).thenReturn(teamModel);

        // Appel à l'API
        mockMvc.perform(post(URI_PATH + "/" + teamId + "/training-sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addTrainingSessionInTeamDTORequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$.id").value(teamDTOResponse.id()))
                .andExpect(jsonPath("$.category").value(teamDTOResponse.category().toString()))
                .andExpect(jsonPath("$.gender").value(teamDTOResponse.gender().toString()));

        // Vérification des interactions
        verify(trainingSessionMapper).toEntity(addTrainingSessionInTeamDTORequest.trainingSessionDTORequest());
        verify(teamService).addTrainingSession(teamId,hallId,trainingSession);
        verify(teamAssembler).toModel(updatedTeam);
    }

    @Test
    @WithMockUser(username = "user", roles = {"NO_ADMIN"})
    void testAddTrainingSession_AccessDenied_WithoutAdminRole() throws Exception {
        long teamId = 1L;
        long hallId = 1L;
        AddTrainingSessionInTeamDTORequest addTrainingSessionInTeamDTORequest = new AddTrainingSessionInTeamDTORequest(hallId,new TrainingSessionDTORequest(new TimeSlotDTORequest(DayOfWeek.FRIDAY, LocalTime.of(10,0), LocalTime.of(11,0))));


        mockMvc.perform(post(URI_PATH + "/" + teamId + "/training-sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addTrainingSessionInTeamDTORequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testAddTrainingSession_AccessDenied_WithoutAuthentication() throws Exception {
        long teamId = 1L;
        long hallId = 1L;
        AddTrainingSessionInTeamDTORequest addTrainingSessionInTeamDTORequest = new AddTrainingSessionInTeamDTORequest(hallId,new TrainingSessionDTORequest(new TimeSlotDTORequest(DayOfWeek.FRIDAY, LocalTime.of(10,0), LocalTime.of(11,0)))); 

        mockMvc.perform(post(URI_PATH + "/" + teamId + "/training-sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addTrainingSessionInTeamDTORequest)))
                .andExpect(status().isUnauthorized());
    }

    private static TeamDTOCreateRequest getTeamDTOCreateRequest() {
        return new TeamDTOCreateRequest(Gender.M, Category.U11, 1073741824);
    }


    private static TeamDTOResponse getTeamDTOResponse(long id) {
        return TeamDTOResponse.builder()
                .id(id)
                .gender(Gender.M)
                .category(Category.U11)
                .teamNumber(1073741824)
                .build();
    }


    private static TeamDTOUpdateRequest getTeamDTOUpdateRequest() {
        return new TeamDTOUpdateRequest(Gender.M, Category.U11, 1073741825);
    }


    private static Team getTeam() {
        return Team.builder()
                .teamNumber(1)
                .category(Category.U11)
                .gender(Gender.M)
                .build(); // Remplissez les propriétés comme dans votre entité Team
    }

    private static Team getTeam(long id) {
        Team team = getTeam();
        team.setId(id);
        return team;
    }
}