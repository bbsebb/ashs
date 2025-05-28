package fr.hoenheimsports.trainingservice.integrationTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.hoenheimsports.trainingservice.TestcontainersConfiguration;
import fr.hoenheimsports.trainingservice.config.TestSecurityConfig;
import fr.hoenheimsports.trainingservice.dto.request.TeamDTOCreateRequest;
import fr.hoenheimsports.trainingservice.dto.request.TeamDTOUpdateRequest;
import fr.hoenheimsports.trainingservice.model.Category;
import fr.hoenheimsports.trainingservice.model.Gender;
import fr.hoenheimsports.trainingservice.model.Team;
import fr.hoenheimsports.trainingservice.repository.TeamRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Import({TestcontainersConfiguration.class,TestSecurityConfig.class})
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TeamIntegrationTest {

    private static final String BASE_URI = "/api/teams" ;

    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private Team team1 = new Team();
    private Team team2 = new Team();

    @BeforeEach
    void setUp() {
        var t1 =   Team.builder()
                .teamNumber(1)
                .gender(Gender.M)
                .category(Category.U11)
                .build();
        var t2 = Team.builder()
                .teamNumber(2)
                .gender(Gender.F)
                .category(Category.U13)
                .build();
        team1 = teamRepository.save(t1);
        team2 = teamRepository.save(t2);
    }

    @AfterEach
    void tearDown() {
        teamRepository.deleteAll();
    }

    @Test
    @DisplayName("Get all teams as Admin")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void getTeams_WithRoleAdmin() throws Exception {
        //Arrange
        var page = 0;
        var size = 10;
        var team1BasePath = teamBasePath(0);
        var team2BasePath = teamBasePath(1);
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
                .andExpect(jsonPath("$._links.self.href").value(String.format("http://localhost/api/teams?page=%d&size=%d",page,size)))

                // Verifying high-level templates
                .andExpect(templatesCreateTeam(teamBasePath()))

                // Vérification des teams dans _embedded
                .andExpect(jsonPath("$._embedded.teams.length()").value(2))

                // Team 1
                .andExpect(team1(team1BasePath))
                .andExpect(templatesDeleteTeam(team1BasePath))
                .andExpect(templatesUpdateTeam(team1BasePath))

                // Verifying Team 2
                .andExpect(team2(team2BasePath))
                .andExpect(templatesDeleteTeam(team2BasePath))
                .andExpect(templatesUpdateTeam(team2BasePath));
    }

    @Test
    @DisplayName("Get all teams as User")
    @WithMockUser(username = "user", roles = {"NO_NO_USER"})
    public void getTeams_WithoutRoleAdmin() throws Exception {
        //Arrange
        var page = 0;
        var size = 10;
        var team1BasePath = teamBasePath(0);
        var team2BasePath = teamBasePath(1);
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
                .andExpect(jsonPath("$._links.self.href").value(String.format("http://localhost/api/teams?page=%d&size=%d", page, size)))

                // Verifying high-level templates
                .andExpect(jsonPath("$._templates.createTeam").doesNotExist())

                // Verifying teams in _embedded
                .andExpect(jsonPath("$._embedded.teams.length()").value(2))

                // Team 1
                .andExpect(team1(team1BasePath))
                .andExpect( jsonPath(teamBasePath(0) + "._templates.deleteTeam").doesNotExist())
                .andExpect(jsonPath(teamBasePath(0) + "._templates.updateTeam").doesNotExist())

                //Team 2
                .andExpect(team2(team2BasePath))
                .andExpect( jsonPath(teamBasePath(1) + "._templates.deleteTeam").doesNotExist())
                .andExpect(jsonPath(teamBasePath(1) + "._templates.updateTeam").doesNotExist());
    }

    @Test
    @DisplayName("Get all teams without authentication")
    public void getTeams_WithoutAuthentication() throws Exception {
        //Arrange
        var page = 0;
        var size = 10;
        var team1BasePath = teamBasePath(0);
        var team2BasePath = teamBasePath(1);

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
                .andExpect(jsonPath("$._links.self.href").value(String.format("http://localhost/api/teams?page=%d&size=%d",page,size)))

                // Vérification des templates haut niveau
                .andExpect(jsonPath("$._templates.createTeam").doesNotExist())

                // Vérification des teams dans _embedded
                .andExpect(jsonPath("$._embedded.teams.length()").value(2))

                // Verifying Team 1
                .andExpect(team1(team1BasePath))
                .andExpect( jsonPath(teamBasePath(0) + "._templates.deleteTeam").doesNotExist())
                .andExpect(jsonPath(teamBasePath(0) + "._templates.updateTeam").doesNotExist())

                //Team 2
                .andExpect(team2(team2BasePath))
                .andExpect( jsonPath(teamBasePath(1) + "._templates.deleteTeam").doesNotExist())
                .andExpect(jsonPath(teamBasePath(1) + "._templates.updateTeam").doesNotExist());
    }

    @Test
    @DisplayName("Get team by ID as Admin")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void getTeamById_WithRoleAdmin() throws Exception {
        //Arrange
        var page = 0;
        var size = 10;
        var teamBasePath = teamBasePath();
        //Act & Assert
        mockMvc.perform(get(BASE_URI + "/" +team1.getId()).param("page",String.valueOf(page)).param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/prs.hal-forms+json"))

                // Team 1
                .andExpect(team1(teamBasePath))
                .andExpect(templatesDeleteTeam(teamBasePath))
                .andExpect(templatesUpdateTeam(teamBasePath));
    }

    @Test
    @DisplayName("Get team by ID as User")
    @WithMockUser(username = "user", roles = {"NO_USER"})
    public void getTeamById_WithoutRoleAdmin() throws Exception {
        //Arrange
        var page = 0;
        var size = 10;
        var teamBasePath = teamBasePath();
        //Act & Assert
        mockMvc.perform(get(BASE_URI + "/" +team1.getId()).param("page",String.valueOf(page)).param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/prs.hal-forms+json"))

                // Team 1
                .andExpect(team1(teamBasePath))
                .andExpect( jsonPath(teamBasePath() + "._templates.deleteTeam").doesNotExist())
                .andExpect(jsonPath(teamBasePath() + "._templates.updateTeam").doesNotExist());
    }

    @Test
    @DisplayName("Get team by ID without authentication")
    public void getTeamById_WithoutAuthentication() throws Exception {
        //Arrange
        var page = 0;
        var size = 10;
        var teamBasePath = teamBasePath();
        //Act & Assert
        mockMvc.perform(get(BASE_URI + "/" +team1.getId()).param("page",String.valueOf(page)).param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/prs.hal-forms+json"))

                // Team 1
                .andExpect(team1(teamBasePath))
                .andExpect( jsonPath(teamBasePath() + "._templates.deleteTeam").doesNotExist())
                .andExpect(jsonPath(teamBasePath() + "._templates.updateTeam").doesNotExist());
    }


    @Test
    @DisplayName("Get team by ID when it doesn't exist")
    public void getTeamById_WhenTeamOdDoesNotExist_ShouldReturn404() throws Exception {
        //Arrange
        var page = 0;
        var size = 10;
        var id = team1.getId() + team2.getId();  // Use ID that doesn't exist
        //Act & Assert
        mockMvc.perform(get(BASE_URI + "/"  + id).param("page", String.valueOf(page)).param("size", String.valueOf(size)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/prs.hal-forms+json"));
    }

    @Test
    @DisplayName("Create a new team with valid data as Admin")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void createTeam_WithValidTeam() throws Exception {
        //Arrange
        TeamDTOCreateRequest teamDTORequest = getTeamDTOCreateRequest();
        var teamBasePath = teamBasePath();
        //Act & Assert
        mockMvc.perform(post(BASE_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamDTORequest)))
                .andExpect(status().isCreated())
                .andExpect(team(teamBasePath(), null, teamDTORequest.teamNumber(), teamDTORequest.gender(), teamDTORequest.category()))
                .andExpect(templatesDeleteTeam(teamBasePath))
                .andExpect(templatesUpdateTeam(teamBasePath));

        verifyNumberOfTeams(3);
    }

    @Test
    @DisplayName("Create a new team as User (Forbidden)")
    @WithMockUser(username = "user", roles = {"NO_USER"})
    public void createTeam_WithoutRoleAdmin() throws Exception {
        //Arrange
        TeamDTOCreateRequest teamDTORequest = getTeamDTOCreateRequest();
        //Act & Assert
        mockMvc.perform(post(BASE_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamDTORequest)))
                .andExpect(status().isForbidden());
        verifyNumberOfTeams(2);
    }

    @Test
    @DisplayName("Create a new team without authentication (Unauthorized)")
    public void createTeam_WithoutAuthentication() throws Exception {
        //Arrange
        TeamDTOCreateRequest teamDTORequest = getTeamDTOCreateRequest();
        //Act & Assert
        mockMvc.perform(post(BASE_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamDTORequest)))
                .andExpect(status().isUnauthorized());
        verifyNumberOfTeams(2);
    }

    @Test
    @DisplayName("Create a new team with invalid data (Bad Request)")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void createTeam_WhenArgumentNotValid() throws Exception {
        //Arrange
        TeamDTOCreateRequest teamDTORequest = new TeamDTOCreateRequest(Gender.M,Category.EDH,0);
        //Act & Assert
        mockMvc.perform(post(BASE_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamDTORequest)))
                .andExpect(status().isBadRequest());
        verifyNumberOfTeams(2);
    }

    private void verifyNumberOfTeams(int n) throws Exception {
        mockMvc.perform(get(BASE_URI))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.teams.length()").value(n));
    }

    @Test
    @DisplayName("Update a team with valid data as Admin")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void updateTeam_Success_WithValidData() throws Exception {
        // Arrange : Préparation du DTO contenant les données valides
        TeamDTOUpdateRequest updatedTeamDTO = getValidDTOUpdateRequest();
        String updatedTeamDTOJson = this.objectMapper.writeValueAsString(updatedTeamDTO);
        Long existingTeamId = team1.getId(); // An existing team preconfigured in setUp()
        var teamBasePath = teamBasePath();
        // Act & Assert: Sending the PUT request with assertions on the response
        this.mockMvc.perform(put(BASE_URI + "/" + existingTeamId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedTeamDTOJson))
                .andExpect(status().isOk()) // Verifies a 200 status
                .andExpect(team(teamBasePath(),existingTeamId, updatedTeamDTO.teamNumber(), updatedTeamDTO.gender(), updatedTeamDTO.category()))
                .andExpect(templatesDeleteTeam(teamBasePath))
                .andExpect(templatesUpdateTeam(teamBasePath));
        this.mockMvc.perform(get(BASE_URI + "/" + existingTeamId))
                .andExpect(status().isOk())
                .andExpect(team(teamBasePath(),existingTeamId, updatedTeamDTO.teamNumber(), updatedTeamDTO.gender(), updatedTeamDTO.category()))
                .andExpect(templatesDeleteTeam(teamBasePath))
                .andExpect(templatesUpdateTeam(teamBasePath));
        verifyIfTeamExistWithId(team2.getId(),teamBasePath());
    }

    @Test
    @DisplayName("Update a team as User (Forbidden)")
    @WithMockUser(username = "user", roles = {"NO_USER"})
    public void updateTeam_Fails_WithoutRoleAdmin() throws Exception {
        // Arrange : Préparation du DTO contenant les données valides
        TeamDTOUpdateRequest updatedTeamDTO = getValidDTOUpdateRequest();
        String updatedTeamDTOJson = this.objectMapper.writeValueAsString(updatedTeamDTO);
        Long existingTeamId = team1.getId(); // Une salle existante préconfigurée dans setUp()

        // Act & Assert: Sending the PUT request with assertions on the response (FORBIDDEN)
        this.mockMvc.perform(put(BASE_URI + "/" + existingTeamId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedTeamDTOJson))
                .andExpect(status().isForbidden()); // Verifies a 403 status for unauthorized role
        verifyIfTeamExistWithId(existingTeamId,teamBasePath());
    }

    private static TeamDTOUpdateRequest getValidDTOUpdateRequest() {
        return new TeamDTOUpdateRequest(Gender.M,Category.EDH,3);
    }

    @Test
    @DisplayName("Update a team without authentication (Unauthorized)")
    public void updateTeam_Fails_WithoutAuthentication() throws Exception {
        // Arrange: Preparing the DTO containing valid data
        TeamDTOUpdateRequest updatedTeamDTO = getValidDTOUpdateRequest();
        String updatedTeamDTOJson = this.objectMapper.writeValueAsString(updatedTeamDTO);
        Long existingTeamId = team1.getId(); // Une salle existante préconfigurée dans setUp()

        // Act & Assert : Envoie de la requête PUT avec des assertions sur la réponse (UNAUTHORIZED)
        this.mockMvc.perform(put(BASE_URI + "/" + existingTeamId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedTeamDTOJson))
                .andExpect(status().isUnauthorized()); // Vérifie un statut 401 pour utilisateur non authentifié
        verifyIfTeamExistWithId(existingTeamId,teamBasePath());
    }

    @Test
    @DisplayName("Update a team with invalid data (Bad Request)")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void updateTeam_Fails_WithInvalidData() throws Exception {
        // Arrange : Création d'un DTO avec des données non valides (par exemple, sans nom)
        TeamDTOUpdateRequest invalidTeamDTO = new TeamDTOUpdateRequest(Gender.M,Category.EDH,0);
        String invalidTeamDTOJson = this.objectMapper.writeValueAsString(invalidTeamDTO);
        Long existingTeamId = team1.getId(); // Une salle existante préconfigurée dans setUp()

        // Act & Assert: Sending the PUT request with assertions on the response (BAD_REQUEST)
        this.mockMvc.perform(put(BASE_URI + "/" + existingTeamId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidTeamDTOJson))
                .andExpect(status().isBadRequest()); // Verifies a 400 status for invalid data
        verifyIfTeamExistWithId(existingTeamId,teamBasePath());
    }

    @Test
    @DisplayName("Delete a team as Admin")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void deleteTeam_Success_WithRoleAdmin() throws Exception {
        // Arrange : ID d'un Team existant (préconfiguré dans setUp())
        Long existingTeamId = team1.getId();

        // Act & Assert: Successful deletion
        this.mockMvc.perform(delete(BASE_URI + "/" + existingTeamId))
                .andExpect(status().isNoContent()); // Vérifie un statut 204 No Content
        this.mockMvc.perform(get(BASE_URI + "/" + existingTeamId))
                .andExpect(status().isNotFound());
        verifyIfTeamExistWithId(team2.getId(),teamBasePath());
    }

    @Test
    @DisplayName("Delete a team as User (Forbidden)")
    @WithMockUser(username = "user", roles = {"NO_USER"})
    public void deleteTeam_Fails_WithoutRoleAdmin() throws Exception {
        // Arrange : ID d'un Team existant (préconfiguré dans setUp())
        Long existingTeamId = team1.getId();

        // Act & Assert: Attempted deletion by a non-admin user
        this.mockMvc.perform(delete(BASE_URI + "/" + existingTeamId))
                .andExpect(status().isForbidden()); // Vérifie un statut 403 Forbidden
        verifyIfTeamExistWithId(existingTeamId,teamBasePath());
    }

    @Test
    @DisplayName("Delete a team without authentication (Unauthorized)")
    public void deleteTeam_Fails_WithoutAuthentication() throws Exception {
        // Arrange: ID of an existing team (preconfigured in setUp())
        Long existingTeamId = team1.getId();

        // Act & Assert: Tentative de suppression sans authentification
        this.mockMvc.perform(delete(BASE_URI + "/" + existingTeamId))
                .andExpect(status().isUnauthorized()); // Vérifie un statut 401 Unauthorized
        verifyIfTeamExistWithId(existingTeamId,teamBasePath());
    }



    @Test
    @DisplayName("Delete a team when it doesn't exist")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void deleteTeam_Fails_WhenTeamDoesNotExist() throws Exception {
        // Arrange: ID d'un Team inexistant
        long nonExistentTeamId = team1.getId() + team2.getId();

        // Act & Assert: Attempted deletion when the team ID does not exist
        this.mockMvc.perform(delete(BASE_URI + "/" + nonExistentTeamId))
                .andExpect(status().isNotFound()); // Vérifie un statut 404 Not Found
    }



    private void verifyIfTeamExistWithId(long existingTeamId,String teamBasePath) throws Exception {
        teamBasePath = (teamBasePath==null)? teamBasePath() : teamBasePath;
        if(existingTeamId == team1.getId()) {
            this.mockMvc.perform(get(BASE_URI + "/" + existingTeamId))
                    .andExpect(status().isOk())
                    .andExpect(team1(teamBasePath));
        } else if(existingTeamId == team2.getId()) {
            this.mockMvc.perform(get(BASE_URI + "/" + existingTeamId))
                    .andExpect(status().isOk())
                    .andExpect(team2(teamBasePath));
        } else {
            throw new Exception("The team ID does not exist");
        }

    }

    private static TeamDTOCreateRequest getTeamDTOCreateRequest() {
        return new TeamDTOCreateRequest(Gender.F,Category.EDH,3);
    }

    private String teamBasePath(int index) {
        return String.format("$._embedded.teams[%d]", index);
    }
    private String teamBasePath() {
        return "$";
    }


    private ResultMatcher team(String teamBasePath, Long id, int teamNumber, Gender gender, Category category) {
        return resultActions -> {
            if(id != null) {
                jsonPath(teamBasePath + ".id").value(id).match(resultActions);
                jsonPath(teamBasePath + "._links.self.href").value("http://localhost/api/teams/" + id).match(resultActions);
            }

            jsonPath(teamBasePath + ".teamNumber").value(teamNumber).match(resultActions);
            jsonPath(teamBasePath + ".gender").value(gender.toString()).match(resultActions);
            jsonPath(teamBasePath + ".category").value(category.toString()).match(resultActions);
            jsonPath(teamBasePath + "._links.self.href").value(Matchers.matchesPattern("http://localhost/api/teams/" + "\\d+")).match(resultActions);
            jsonPath(teamBasePath + "._links.teams.href").value("http://localhost/api/teams").match(resultActions);
        };
    }



    private ResultMatcher team1(String teamBasePath) {
        return team(teamBasePath, team1.getId(), team1.getTeamNumber(), team1.getGender(), team1.getCategory());
    }

    private ResultMatcher team2(String teamBasePath) {
        return team(teamBasePath, team2.getId(),  team2.getTeamNumber(), team2.getGender(), team2.getCategory());
    }

    private ResultMatcher templatesDeleteTeam(String basePath) {
        var basePathTemplates = String.format("%s._templates.deleteTeam", basePath);
        return resultActions -> {
            jsonPath(basePathTemplates + ".method").value("DELETE").match(resultActions);
            jsonPath(basePathTemplates + ".properties").isEmpty().match(resultActions);
        };
    }

    private ResultMatcher templatesUpdateTeam(String basePath) {
        var basePathTemplates = String.format("%s._templates.updateTeam", basePath);
        return resultActions -> {
            jsonPath(basePathTemplates + ".method").value("PUT").match(resultActions);
            jsonPath(basePathTemplates + ".properties.length()").value(3).match(resultActions);
            jsonPath(basePathTemplates + ".properties[0].name").value("category").match(resultActions);
            jsonPath(basePathTemplates + ".properties[0].readOnly").value(true).match(resultActions);
            jsonPath(basePathTemplates + ".properties[0].required").value(true).match(resultActions);

            jsonPath(basePathTemplates + ".properties[1].name").value("gender").match(resultActions);
            jsonPath(basePathTemplates + ".properties[1].readOnly").value(true).match(resultActions);
            jsonPath(basePathTemplates + ".properties[1].required").value(true).match(resultActions);

            jsonPath(basePathTemplates + ".properties[2].name").value("teamNumber").match(resultActions);
            jsonPath(basePathTemplates + ".properties[2].readOnly").value(true).match(resultActions);
            jsonPath(basePathTemplates + ".properties[2].type").value("number").match(resultActions);

        };
    }

    private ResultMatcher templatesCreateTeam(String basePath) {
        var basePathTemplates = String.format("%s._templates.createTeam", basePath);
        return resultActions -> {
            jsonPath(basePathTemplates + ".method").value("POST").match(resultActions);
            jsonPath(basePathTemplates + ".target").value("http://localhost/api/teams").match(resultActions);
            jsonPath(basePathTemplates + ".properties.length()").value(3).match(resultActions);

            jsonPath(basePathTemplates + ".properties[0].name").value("category").match(resultActions);
            jsonPath(basePathTemplates + ".properties[0].readOnly").value(true).match(resultActions);
            jsonPath(basePathTemplates + ".properties[0].required").value(true).match(resultActions);

            jsonPath(basePathTemplates + ".properties[1].name").value("gender").match(resultActions);
            jsonPath(basePathTemplates + ".properties[1].readOnly").value(true).match(resultActions);
            jsonPath(basePathTemplates + ".properties[1].required").value(true).match(resultActions);

            jsonPath(basePathTemplates + ".properties[2].name").value("teamNumber").match(resultActions);
            jsonPath(basePathTemplates + ".properties[2].readOnly").value(true).match(resultActions);
            jsonPath(basePathTemplates + ".properties[2].type").value("number").match(resultActions);

        };
    }




  /*
              {
  "_embedded": {
    "teamDTOResponseList": [
      {
        "id": 1,
        "gender": "F",
        "category": "U11",
        "teamNumber": 1073741824,
        "_links": {
          "teams": {
            "href": "http://localhost:8082/api/teams"
          },
          "self": {
            "href": "http://localhost:8082/api/teams/1"
          }
        },
        "_templates": {
          "default": {
            "method": "DELETE",
            "properties": []
          },
          "updateTeam": {
            "method": "PUT",
            "properties": [
              {
                "name": "category",
                "readOnly": true,
                "required": true
              },
              {
                "name": "gender",
                "readOnly": true,
                "required": true
              },
              {
                "name": "teamNumber",
                "readOnly": true,
                "type": "number"
              }
            ]
          }
        }
      },
      {
        "id": 2,
        "gender": "F",
        "category": "U13",
        "teamNumber": 1073741824,
        "_links": {
          "teams": {
            "href": "http://localhost:8082/api/teams"
          },
          "self": {
            "href": "http://localhost:8082/api/teams/2"
          }
        },
        "_templates": {
          "default": {
            "method": "DELETE",
            "properties": []
          },
          "updateTeam": {
            "method": "PUT",
            "properties": [
              {
                "name": "category",
                "readOnly": true,
                "required": true
              },
              {
                "name": "gender",
                "readOnly": true,
                "required": true
              },
              {
                "name": "teamNumber",
                "readOnly": true,
                "type": "number"
              }
            ]
          }
        }
      }
    ]
  },
  "_links": {
    "self": {
      "href": "http://localhost:8082/api/teams?page=0&size=20"
    }
  },
  "page": {
    "size": 20,
    "totalElements": 2,
    "totalPages": 1,
    "number": 0
  },
  "_templates": {
    "default": {
      "method": "POST",
      "properties": [
        {
          "name": "category",
          "readOnly": true,
          "required": true
        },
        {
          "name": "gender",
          "readOnly": true,
          "required": true
        },
        {
          "name": "teamNumber",
          "readOnly": true,
          "type": "number"
        }
      ],
      "target": "http://localhost:8082/api/teams"
    }
  }
}
   */
}
