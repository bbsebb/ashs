package fr.hoenheimsports.trainingservice.integrationTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.hoenheimsports.trainingservice.TestcontainersConfiguration;
import fr.hoenheimsports.trainingservice.config.TestSecurityConfig;
import fr.hoenheimsports.trainingservice.dto.request.TimeSlotDTORequest;
import fr.hoenheimsports.trainingservice.dto.request.TrainingSessionDTORequest;
import fr.hoenheimsports.trainingservice.model.*;
import fr.hoenheimsports.trainingservice.repository.HallRepository;
import fr.hoenheimsports.trainingservice.repository.TeamRepository;
import fr.hoenheimsports.trainingservice.repository.TrainingSessionRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Import({TestcontainersConfiguration.class, TestSecurityConfig.class})
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TrainingSessionIntegrationTest {

    private static final String BASE_URI = "/api/training-sessions" ;

    @Autowired
    private TrainingSessionRepository trainingSessionRepository;
    @Autowired
    private HallRepository hallRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private TrainingSession trainingSession1 = new TrainingSession();
    private TrainingSession trainingSession2 = new TrainingSession();

    @BeforeEach
    void setUp() {
        var hall1 =   Hall.builder()
                .name("Gymnase")
                .address(
                        Address.builder()
                                .city("Hoenheim")
                                .country("France")
                                .street("rue des vosges")
                                .postalCode("67800")
                                .build())
                .build();
        hall1 = this.hallRepository.save(hall1);
        var hall2 = Hall.builder()
                .name("Gymnase2")
                .address(
                        Address.builder()
                                .city("Hoenheim2")
                                .country("France2")
                                .street("rue des vosges2")
                                .postalCode("67802")
                                .build()
                )
                .build();
        hall2 = this.hallRepository.save(hall2);
        var team1 =   Team.builder()
                .teamNumber(1)
                .gender(Gender.M)
                .category(Category.U11)
                .build();
        team1 = this.teamRepository.save(team1);
        var team2 = Team.builder()
                .teamNumber(2)
                .gender(Gender.F)
                .category(Category.U13)
                .build();
        team2 = this.teamRepository.save(team2);
        var timeSlot1 = TimeSlot.builder()
                .dayOfWeek(DayOfWeek.FRIDAY)
                .startTime(LocalTime.of(10,0))
                .endTime(LocalTime.of(12,0))
                .build();
        var trainingSessionNotPersisted2 =   TrainingSession.builder()
                .timeSlot(timeSlot1)
                .hall(hall1)
                .team(team1)
                .build();
        var timeSlot2 = TimeSlot.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(8,0))
                .endTime(LocalTime.of(9,0))
                .build();
        var trainingSessionNotPersisted1 = TrainingSession.builder()
                .timeSlot(timeSlot2)
                .hall(hall2)
                .team(team2)
                .build();
        trainingSession1 = trainingSessionRepository.save(trainingSessionNotPersisted2);
       trainingSession2 = trainingSessionRepository.save(trainingSessionNotPersisted1);
    }

    @AfterEach
    void tearDown() {
        trainingSessionRepository.deleteAll();
        hallRepository.deleteAll();
        teamRepository.deleteAll();
    }

    @Test
    @DisplayName("Get all trainingSessions as Admin")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void getTrainingSessions_WithRoleAdmin() throws Exception {
        //Arrange
        var page = 0;
        var size = 10;
        var trainingSession1BasePath = trainingSessionBasePath(0);
        var trainingSession2BasePath = trainingSessionBasePath(1);
        //Act & Assert
        mockMvc.perform(get(BASE_URI ).param("page",String.valueOf(page)).param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/prs.hal-forms+json"))

                // Verifying high-level properties
                .andExpect(jsonPath("$._embedded").exists())

                // Vérification des métadonnées (page)
                .andExpect(jsonPath("$.page.number").value(page))
                .andExpect(jsonPath("$.page.size").value(size))
                .andExpect(jsonPath("$.page.totalElements").value(2))
                .andExpect(jsonPath("$.page.totalPages").value(1))

                // Verifying high-level links
                .andExpect(jsonPath("$._links.self.href").value(String.format("http://localhost/api/training-sessions?page=%d&size=%d",page,size)))

                // Verifying high-level templates
                .andExpect(templatesCreateTrainingSession(trainingSessionBasePath()))

                // Vérification des trainingSessions dans _embedded
                .andExpect(jsonPath("$._embedded.trainingSessions.length()").value(2))

                // TrainingSession 1
                .andExpect(trainingSession1(trainingSession1BasePath))
                .andExpect(templatesDeleteTrainingSession(trainingSession1BasePath))
                .andExpect(templatesUpdateTrainingSession(trainingSession1BasePath))

                // Verifying TrainingSession 2
                .andExpect(trainingSession2(trainingSession2BasePath))
                .andExpect(templatesDeleteTrainingSession(trainingSession2BasePath))
                .andExpect(templatesUpdateTrainingSession(trainingSession2BasePath));
    }

    @Test
    @DisplayName("Get all trainingSessions as User")
    @WithMockUser(username = "user", roles = {"NO_NO_USER"})
    public void getTrainingSessions_WithoutRoleAdmin() throws Exception {
        //Arrange
        var page = 0;
        var size = 10;
        var trainingSession1BasePath = trainingSessionBasePath(0);
        var trainingSession2BasePath = trainingSessionBasePath(1);
        //Act & Assert
        mockMvc.perform(get(BASE_URI ).param("page",String.valueOf(page)).param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/prs.hal-forms+json"))

                // Verifying high-level properties
                .andExpect(jsonPath("$._embedded").exists())

                // Verifying metadata (page)
                .andExpect(jsonPath("$.page.number").value(page))
                .andExpect(jsonPath("$.page.size").value(size))
                .andExpect(jsonPath("$.page.totalElements").value(2))
                .andExpect(jsonPath("$.page.totalPages").value(1))

                // Vérification des liens haut niveau
                .andExpect(jsonPath("$._links.self.href").value(String.format("http://localhost/api/training-sessions?page=%d&size=%d", page, size)))

                // Verifying high-level templates
                .andExpect(jsonPath("$._templates.createTrainingSession").doesNotExist())

                // Verifying trainingSessions in _embedded
                .andExpect(jsonPath("$._embedded.trainingSessions.length()").value(2))

                // TrainingSession 1
                .andExpect(trainingSession1(trainingSession1BasePath))
                .andExpect( jsonPath(trainingSessionBasePath(0) + "._templates.deleteTrainingSession").doesNotExist())
                .andExpect(jsonPath(trainingSessionBasePath(0) + "._templates.updateTrainingSession").doesNotExist())

                //TrainingSession 2
                .andExpect(trainingSession2(trainingSession2BasePath))
                .andExpect( jsonPath(trainingSessionBasePath(1) + "._templates.deleteTrainingSession").doesNotExist())
                .andExpect(jsonPath(trainingSessionBasePath(1) + "._templates.updateTrainingSession").doesNotExist());
    }

    @Test
    @DisplayName("Get all trainingSessions without authentication")
    public void getTrainingSessions_WithoutAuthentication() throws Exception {
        //Arrange
        var page = 0;
        var size = 10;
        var trainingSession1BasePath = trainingSessionBasePath(0);
        var trainingSession2BasePath = trainingSessionBasePath(1);

        //Act & Assert
        mockMvc.perform(get(BASE_URI ).param("page",String.valueOf(page)).param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/prs.hal-forms+json"))

                // Vérification des propriétés haut niveau
                .andExpect(jsonPath("$._embedded").exists())

                // Verifying metadata (page)
                .andExpect(jsonPath("$.page.number").value(page))
                .andExpect(jsonPath("$.page.size").value(size))
                .andExpect(jsonPath("$.page.totalElements").value(2))
                .andExpect(jsonPath("$.page.totalPages").value(1))

                // Vérification des liens haut niveau
                .andExpect(jsonPath("$._links.self.href").value(String.format("http://localhost/api/training-sessions?page=%d&size=%d",page,size)))

                // Vérification des templates haut niveau
                .andExpect(jsonPath("$._templates.createTrainingSession").doesNotExist())

                // Vérification des trainingSessions dans _embedded
                .andExpect(jsonPath("$._embedded.trainingSessions.length()").value(2))

                // Verifying TrainingSession 1
                .andExpect(trainingSession1(trainingSession1BasePath))
                .andExpect( jsonPath(trainingSessionBasePath(0) + "._templates.deleteTrainingSession").doesNotExist())
                .andExpect(jsonPath(trainingSessionBasePath(0) + "._templates.updateTrainingSession").doesNotExist())

                //TrainingSession 2
                .andExpect(trainingSession2(trainingSession2BasePath))
                .andExpect( jsonPath(trainingSessionBasePath(1) + "._templates.deleteTrainingSession").doesNotExist())
                .andExpect(jsonPath(trainingSessionBasePath(1) + "._templates.updateTrainingSession").doesNotExist());
    }

    @Test
    @DisplayName("Get trainingSession by ID as Admin")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void getTrainingSessionById_WithRoleAdmin() throws Exception {
        //Arrange
        var page = 0;
        var size = 10;
        var trainingSessionBasePath = trainingSessionBasePath();
        //Act & Assert
        mockMvc.perform(get(BASE_URI + "/" +trainingSession1.getId()).param("page",String.valueOf(page)).param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/prs.hal-forms+json"))

                // TrainingSession 1
                .andExpect(trainingSession1(trainingSessionBasePath))
                .andExpect(templatesDeleteTrainingSession(trainingSessionBasePath))
                .andExpect(templatesUpdateTrainingSession(trainingSessionBasePath));
    }

    @Test
    @DisplayName("Get trainingSession by ID as User")
    @WithMockUser(username = "user", roles = {"NO_USER"})
    public void getTrainingSessionById_WithoutRoleAdmin() throws Exception {
        //Arrange
        var page = 0;
        var size = 10;
        var trainingSessionBasePath = trainingSessionBasePath();
        //Act & Assert
        mockMvc.perform(get(BASE_URI + "/" +trainingSession1.getId()).param("page",String.valueOf(page)).param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/prs.hal-forms+json"))

                // TrainingSession 1
                .andExpect(trainingSession1(trainingSessionBasePath))
                .andExpect( jsonPath(trainingSessionBasePath() + "._templates.deleteTrainingSession").doesNotExist())
                .andExpect(jsonPath(trainingSessionBasePath() + "._templates.updateTrainingSession").doesNotExist());
    }

    @Test
    @DisplayName("Get trainingSession by ID without authentication")
    public void getTrainingSessionById_WithoutAuthentication() throws Exception {
        //Arrange
        var page = 0;
        var size = 10;
        var trainingSessionBasePath = trainingSessionBasePath();
        //Act & Assert
        mockMvc.perform(get(BASE_URI + "/" +trainingSession1.getId()).param("page",String.valueOf(page)).param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/prs.hal-forms+json"))

                // TrainingSession 1
                .andExpect(trainingSession1(trainingSessionBasePath))
                .andExpect( jsonPath(trainingSessionBasePath() + "._templates.deleteTrainingSession").doesNotExist())
                .andExpect(jsonPath(trainingSessionBasePath() + "._templates.updateTrainingSession").doesNotExist());
    }


    @Test
    @DisplayName("Get trainingSession by ID when it doesn't exist")
    public void getTrainingSessionById_WhenTrainingSessionOdDoesNotExist_ShouldReturn404() throws Exception {
        //Arrange
        var page = 0;
        var size = 10;
        var id = trainingSession1.getId() + trainingSession2.getId();  // Use ID that doesn't exist
        //Act & Assert
        mockMvc.perform(get(BASE_URI + "/"  + id).param("page", String.valueOf(page)).param("size", String.valueOf(size)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/prs.hal-forms+json"));
    }

/* On ne peut pas créer, on peut juste l'ajouter à une équipe    @Test
    @DisplayName("Create a new trainingSession with valid data as Admin")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void createTrainingSession_WithValidTrainingSession() throws Exception {
        //Arrange
        var trainingSessionDTORequest = getTrainingSessionDTORequest();
        var trainingSessionBasePath = trainingSessionBasePath();
        //Act & Assert
        mockMvc.perform(post(BASE_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainingSessionDTORequest)))
                .andExpect(status().isCreated())
                .andExpect(trainingSession(trainingSessionBasePath(), null,  trainingSessionDTORequest.timeSlot().dayOfWeek(), trainingSessionDTORequest.timeSlot().startTime(), trainingSessionDTORequest.timeSlot().endTime()))
                .andExpect(templatesDeleteTrainingSession(trainingSessionBasePath))
                .andExpect(templatesUpdateTrainingSession(trainingSessionBasePath));

        verifyNumberOfTrainingSessions(3);
    }

    @Test
    @DisplayName("Create a new trainingSession as User (Forbidden)")
    @WithMockUser(username = "user", roles = {"NO_USER"})
    public void createTrainingSession_WithoutRoleAdmin() throws Exception {
        //Arrange
        var trainingSessionDTORequest = getTrainingSessionDTORequest();
        //Act & Assert
        mockMvc.perform(post(BASE_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainingSessionDTORequest)))
                .andExpect(status().isForbidden());
        verifyNumberOfTrainingSessions(2);
    }

    @Test
    @DisplayName("Create a new trainingSession without authentication (Unauthorized)")
    public void createTrainingSession_WithoutAuthentication() throws Exception {
        //Arrange
        var trainingSessionDTORequest = getTrainingSessionDTORequest();
        //Act & Assert
        mockMvc.perform(post(BASE_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainingSessionDTORequest)))
                .andExpect(status().isUnauthorized());
        verifyNumberOfTrainingSessions(2);
    }

    @Test
    @DisplayName("Create a new trainingSession with invalid data (Bad Request)")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void createTrainingSession_WhenArgumentNotValid() throws Exception {
        //Arrange
        TrainingSessionDTORequest trainingSessionDTORequest =
                TrainingSessionDTORequest.builder()
                        .timeSlot(TimeSlotDTORequest.builder().endTime(LocalTime.of(11,0)).build())
                        .build();
        //Act & Assert
        mockMvc.perform(post(BASE_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainingSessionDTORequest)))
                .andExpect(status().isBadRequest());
        verifyNumberOfTrainingSessions(2);
    }*/

/*    private void verifyNumberOfTrainingSessions(int n) throws Exception {
        mockMvc.perform(get(BASE_URI))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.trainingSessions.length()").value(n));
    }*/

    @Test
    @DisplayName("Update a trainingSession with valid data as Admin")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void updateTrainingSession_Success_WithValidData() throws Exception {
        // Arrange : Préparation du DTO contenant les données valides
        var updatedTrainingSessionDTO = getValidDTOUpdateRequest();
        String updatedTrainingSessionDTOJson = this.objectMapper.writeValueAsString(updatedTrainingSessionDTO);
        Long existingTrainingSessionId = trainingSession1.getId(); // An existing trainingSession preconfigured in setUp()
        var trainingSessionBasePath = trainingSessionBasePath();
        // Act & Assert: Sending the PUT request with assertions on the response
        this.mockMvc.perform(put(BASE_URI + "/" + existingTrainingSessionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedTrainingSessionDTOJson))
                .andExpect(status().isOk()) // Verifies a 200 status
                .andExpect(trainingSession(trainingSessionBasePath(),existingTrainingSessionId, updatedTrainingSessionDTO.timeSlot().dayOfWeek(), updatedTrainingSessionDTO.timeSlot().startTime(), updatedTrainingSessionDTO.timeSlot().endTime()))
                .andExpect(templatesDeleteTrainingSession(trainingSessionBasePath))
                .andExpect(templatesUpdateTrainingSession(trainingSessionBasePath));
        this.mockMvc.perform(get(BASE_URI + "/" + existingTrainingSessionId))
                .andExpect(status().isOk())
                .andExpect(trainingSession(trainingSessionBasePath(),existingTrainingSessionId, updatedTrainingSessionDTO.timeSlot().dayOfWeek(), updatedTrainingSessionDTO.timeSlot().startTime(), updatedTrainingSessionDTO.timeSlot().endTime()))
                .andExpect(templatesDeleteTrainingSession(trainingSessionBasePath))
                .andExpect(templatesUpdateTrainingSession(trainingSessionBasePath));
        verifyIfTrainingSessionExistWithId(trainingSession2.getId(),trainingSessionBasePath());
    }

    @Test
    @DisplayName("Update a trainingSession as User (Forbidden)")
    @WithMockUser(username = "user", roles = {"NO_USER"})
    public void updateTrainingSession_Fails_WithoutRoleAdmin() throws Exception {
        // Arrange : Préparation du DTO contenant les données valides
        var updatedTrainingSessionDTO = getValidDTOUpdateRequest();
        String updatedTrainingSessionDTOJson = this.objectMapper.writeValueAsString(updatedTrainingSessionDTO);
        Long existingTrainingSessionId = trainingSession1.getId(); // Une salle existante préconfigurée dans setUp()

        // Act & Assert: Sending the PUT request with assertions on the response (FORBIDDEN)
        this.mockMvc.perform(put(BASE_URI + "/" + existingTrainingSessionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedTrainingSessionDTOJson))
                .andExpect(status().isForbidden()); // Verifies a 403 status for unauthorized role
        verifyIfTrainingSessionExistWithId(existingTrainingSessionId,trainingSessionBasePath());
    }

    private static TrainingSessionDTORequest getValidDTOUpdateRequest() {
        return TrainingSessionDTORequest.builder()
                .timeSlot(getTimeSlotDTOUpdateRequest())
                .build();
    }

    private static TimeSlotDTORequest getTimeSlotDTOUpdateRequest() {
        return TimeSlotDTORequest.builder()
                .endTime(LocalTime.of(14, 0,0))
                .startTime(LocalTime.of(13, 0,0))
                .dayOfWeek(DayOfWeek.SUNDAY)
                .build();
    }

    @Test
    @DisplayName("Update a trainingSession without authentication (Unauthorized)")
    public void updateTrainingSession_Fails_WithoutAuthentication() throws Exception {
        // Arrange: Preparing the DTO containing valid data
        var updatedTrainingSessionDTO = getValidDTOUpdateRequest();
        String updatedTrainingSessionDTOJson = this.objectMapper.writeValueAsString(updatedTrainingSessionDTO);
        Long existingTrainingSessionId = trainingSession1.getId(); // Une salle existante préconfigurée dans setUp()

        // Act & Assert : Envoie de la requête PUT avec des assertions sur la réponse (UNAUTHORIZED)
        this.mockMvc.perform(put(BASE_URI + "/" + existingTrainingSessionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedTrainingSessionDTOJson))
                .andExpect(status().isUnauthorized()); // Vérifie un statut 401 pour utilisateur non authentifié
        verifyIfTrainingSessionExistWithId(existingTrainingSessionId,trainingSessionBasePath());
    }

    @Test
    @DisplayName("Update a trainingSession with invalid data (Bad Request)")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void updateTrainingSession_Fails_WithInvalidData() throws Exception {
        // Arrange : Création d'un DTO avec des données non valides (par exemple, sans nom)
        var invalidTrainingSessionDTO =
                TrainingSessionDTORequest.builder()
                        .timeSlot(TimeSlotDTORequest.builder().endTime(LocalTime.of(11,0)).build())
                        .build();
        String invalidTrainingSessionDTOJson = this.objectMapper.writeValueAsString(invalidTrainingSessionDTO);
        Long existingTrainingSessionId = trainingSession1.getId(); // Une salle existante préconfigurée dans setUp()

        // Act & Assert: Sending the PUT request with assertions on the response (BAD_REQUEST)
        this.mockMvc.perform(put(BASE_URI + "/" + existingTrainingSessionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidTrainingSessionDTOJson))
                .andExpect(status().isBadRequest()); // Verifies a 400 status for invalid data
        verifyIfTrainingSessionExistWithId(existingTrainingSessionId,trainingSessionBasePath());
    }

    @Test
    @DisplayName("Delete a trainingSession as Admin")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void deleteTrainingSession_Success_WithRoleAdmin() throws Exception {
        // Arrange : ID d'un TrainingSession existant (préconfiguré dans setUp())
        Long existingTrainingSessionId = trainingSession1.getId();

        // Act & Assert: Successful deletion
        this.mockMvc.perform(delete(BASE_URI + "/" + existingTrainingSessionId))
                .andExpect(status().isNoContent()); // Vérifie un statut 204 No Content
        this.mockMvc.perform(get(BASE_URI + "/" + existingTrainingSessionId))
                .andExpect(status().isNotFound());
        verifyIfTrainingSessionExistWithId(trainingSession2.getId(),trainingSessionBasePath());
    }

    @Test
    @DisplayName("Delete a trainingSession as User (Forbidden)")
    @WithMockUser(username = "user", roles = {"NO_USER"})
    public void deleteTrainingSession_Fails_WithoutRoleAdmin() throws Exception {
        // Arrange : ID d'un TrainingSession existant (préconfiguré dans setUp())
        Long existingTrainingSessionId = trainingSession1.getId();

        // Act & Assert: Attempted deletion by a non-admin user
        this.mockMvc.perform(delete(BASE_URI + "/" + existingTrainingSessionId))
                .andExpect(status().isForbidden()); // Vérifie un statut 403 Forbidden
        verifyIfTrainingSessionExistWithId(existingTrainingSessionId,trainingSessionBasePath());
    }

    @Test
    @DisplayName("Delete a trainingSession without authentication (Unauthorized)")
    public void deleteTrainingSession_Fails_WithoutAuthentication() throws Exception {
        // Arrange: ID of an existing trainingSession (preconfigured in setUp())
        Long existingTrainingSessionId = trainingSession1.getId();

        // Act & Assert: Tentative de suppression sans authentification
        this.mockMvc.perform(delete(BASE_URI + "/" + existingTrainingSessionId))
                .andExpect(status().isUnauthorized()); // Vérifie un statut 401 Unauthorized
        verifyIfTrainingSessionExistWithId(existingTrainingSessionId,trainingSessionBasePath());
    }



    @Test
    @DisplayName("Delete a trainingSession when it doesn't exist")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void deleteTrainingSession_Fails_WhenTrainingSessionDoesNotExist() throws Exception {
        // Arrange: ID d'un TrainingSession inexistant
        long nonExistentTrainingSessionId = trainingSession1.getId() + trainingSession2.getId();

        // Act & Assert: Attempted deletion when the trainingSession ID does not exist
        this.mockMvc.perform(delete(BASE_URI + "/" + nonExistentTrainingSessionId))
                .andExpect(status().isNotFound()); // Vérifie un statut 404 Not Found
    }



    private void verifyIfTrainingSessionExistWithId(long existingTrainingSessionId,String trainingSessionBasePath) throws Exception {
        trainingSessionBasePath = (trainingSessionBasePath==null)? trainingSessionBasePath() : trainingSessionBasePath;
        if(existingTrainingSessionId == trainingSession1.getId()) {
            this.mockMvc.perform(get(BASE_URI + "/" + existingTrainingSessionId))
                    .andExpect(status().isOk())
                    .andExpect(trainingSession1(trainingSessionBasePath));
        } else if(existingTrainingSessionId == trainingSession2.getId()) {
            this.mockMvc.perform(get(BASE_URI + "/" + existingTrainingSessionId))
                    .andExpect(status().isOk())
                    .andExpect(trainingSession2(trainingSessionBasePath));
        } else {
            throw new Exception("The trainingSession ID does not exist");
        }

    }



/*    private static TrainingSessionDTORequest getTrainingSessionDTORequest() {
        return TrainingSessionDTORequest.builder()
                .timeSlot(getTimeSlotDTORequest())
                .build();
    }*/

/*    private static TimeSlotDTORequest getTimeSlotDTORequest() {
        return TimeSlotDTORequest.builder()
                .endTime(LocalTime.of(12, 0,0))
                .startTime(LocalTime.of(10, 0,0))
                .dayOfWeek(DayOfWeek.FRIDAY)
                .build();
    }*/



    private String trainingSessionBasePath(int index) {
        return String.format("$._embedded.trainingSessions[%d]", index);
    }
    private String trainingSessionBasePath() {
        return "$";
    }


    private ResultMatcher trainingSession(String trainingSessionBasePath, Long id, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        return resultActions -> {
            if(id != null) {
                jsonPath(trainingSessionBasePath + ".id").value(id).match(resultActions);
                jsonPath(trainingSessionBasePath + "._links.self.href").value("http://localhost/api/training-sessions/" + id).match(resultActions);
            }

            jsonPath(trainingSessionBasePath + ".timeSlot.dayOfWeek").value(dayOfWeek.toString()).match(resultActions);
            jsonPath(trainingSessionBasePath + ".timeSlot.startTime").value(startTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"))).match(resultActions);
            jsonPath(trainingSessionBasePath + ".timeSlot.endTime").value(endTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"))).match(resultActions);
            jsonPath(trainingSessionBasePath + "._links.self.href").value(Matchers.matchesPattern("http://localhost/api/training-sessions/" + "\\d+")).match(resultActions);
            jsonPath(trainingSessionBasePath + "._links.trainingSessions.href").value("http://localhost/api/training-sessions").match(resultActions);
        };
    }



    private ResultMatcher trainingSession1(String trainingSessionBasePath) {
        return trainingSession(trainingSessionBasePath, trainingSession1.getId(), trainingSession1.getTimeSlot().getDayOfWeek(), trainingSession1.getTimeSlot().getStartTime(), trainingSession1.getTimeSlot().getEndTime());
    }

    private ResultMatcher trainingSession2(String trainingSessionBasePath) {
        return trainingSession(trainingSessionBasePath, trainingSession2.getId(), trainingSession2.getTimeSlot().getDayOfWeek(), trainingSession2.getTimeSlot().getStartTime(), trainingSession2.getTimeSlot().getEndTime());
    }

    private ResultMatcher templatesDeleteTrainingSession(String basePath) {
        var basePathTemplates = String.format("%s._templates.deleteTrainingSession", basePath);
        return resultActions -> {
            jsonPath(basePathTemplates + ".method").value("DELETE").match(resultActions);
            jsonPath(basePathTemplates + ".properties").isEmpty().match(resultActions);
        };
    }

    private ResultMatcher templatesUpdateTrainingSession(String basePath) {
        var basePathTemplates = String.format("%s._templates.updateTrainingSession", basePath);
        return resultActions -> {
            jsonPath(basePathTemplates + ".method").value("PUT").match(resultActions);
            jsonPath(basePathTemplates + ".properties.length()").value(1).match(resultActions);

            jsonPath(basePathTemplates + ".properties[0].name").value("timeSlot").match(resultActions);
            jsonPath(basePathTemplates + ".properties[0].readOnly").value(true).match(resultActions);
            jsonPath(basePathTemplates + ".properties[0].required").value(true).match(resultActions);
        };
    }

    private ResultMatcher templatesCreateTrainingSession(String basePath) {
        var basePathTemplates = String.format("%s._templates.createTrainingSession", basePath);
        return resultActions -> {
            jsonPath(basePathTemplates + ".method").value("POST").match(resultActions);
            jsonPath(basePathTemplates + ".target").value("http://localhost/api/training-sessions").match(resultActions);
            jsonPath(basePathTemplates + ".properties.length()").value(1).match(resultActions);

            jsonPath(basePathTemplates + ".properties[0].name").value("timeSlot").match(resultActions);
            jsonPath(basePathTemplates + ".properties[0].readOnly").value(true).match(resultActions);
            jsonPath(basePathTemplates + ".properties[0].required").value(true).match(resultActions);
        };
    }


}
/*
 {
  "_embedded": {
    "trainingSessions": [
      {
        "id": 1,
        "timeSlot": {
          "dayOfWeek": "FRIDAY",
          "startTime": "10:00:00",
          "endTime": "12:00:00"
        },
        "hall": {
          "id": 1,
          "name": "Gymnase",
          "address": {
            "street": "rue des vosges",
            "city": "Hoenheim",
            "postalCode": "67800",
            "country": "France"
          }
        },
        "_links": {
          "trainingSessions": {
            "href": "http://localhost/api/training-sessions"
          },
          "self": {
            "href": "http://localhost/api/training-sessions/1"
          }
        },
        "_templates": {
          "deleteTrainingSession": {
            "method": "DELETE",
            "properties": []
          },
          "updateTrainingSession": {
            "method": "PUT",
            "properties": [
              {
                "name": "timeSlot",
                "readOnly": true,
                "required": true
              }
            ]
          },
          "default": {
            "method": "DELETE",
            "properties": []
          }
        }
      },
      {
        "id": 2,
        "timeSlot": {
          "dayOfWeek": "MONDAY",
          "startTime": "08:00:00",
          "endTime": "09:00:00"
        },
        "hall": {
          "id": 2,
          "name": "Gymnase2",
          "address": {
            "street": "rue des vosges2",
            "city": "Hoenheim2",
            "postalCode": "67802",
            "country": "France2"
          }
        },
        "_links": {
          "trainingSessions": {
            "href": "http://localhost/api/training-sessions"
          },
          "self": {
            "href": "http://localhost/api/training-sessions/2"
          }
        },
        "_templates": {
          "deleteTrainingSession": {
            "method": "DELETE",
            "properties": []
          },
          "updateTrainingSession": {
            "method": "PUT",
            "properties": [
              {
                "name": "timeSlot",
                "readOnly": true,
                "required": true
              }
            ]
          },
          "default": {
            "method": "DELETE",
            "properties": []
          }
        }
      }
    ]
  },
  "_links": {
    "self": {
      "href": "http://localhost/api/training-sessions?page=0&size=10"
    }
  },
  "page": {
    "size": 10,
    "totalElements": 2,
    "totalPages": 1,
    "number": 0
  },
  "_templates": {
    "default": {
      "method": "POST",
      "properties": [
        {
          "name": "timeSlot",
          "readOnly": true,
          "required": true
        }
      ],
      "target": "http://localhost/api/training-sessions"
    },
    "createTrainingSession": {
      "method": "POST",
      "properties": [
        {
          "name": "timeSlot",
          "readOnly": true,
          "required": true
        }
      ],
      "target": "http://localhost/api/training-sessions"
    }
  }
}

 */