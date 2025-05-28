package fr.hoenheimsports.trainingservice.integrationTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.hoenheimsports.trainingservice.TestcontainersConfiguration;
import fr.hoenheimsports.trainingservice.config.TestSecurityConfig;
import fr.hoenheimsports.trainingservice.dto.request.CoachDTORequest;
import fr.hoenheimsports.trainingservice.model.Coach;
import fr.hoenheimsports.trainingservice.repository.CoachRepository;
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
@Import({TestcontainersConfiguration.class, TestSecurityConfig.class})
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CoachIntegrationTest {


    private static final String BASE_URI = "/api/coaches";

    @Autowired
    private CoachRepository coachRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private Coach coach1 = new Coach();
    private Coach coach2 = new Coach();

    @BeforeEach
    void setUp() {
        var c1 = Coach.builder()
                .name("John")
                .surname("Doe")
                .email("john.doe@example.com")
                .phone("+33123456781")
                .build();

        var c2 = Coach.builder()
                .name("John2")
                .surname("Doe2")
                .email("john.doe@example.com2")
                .phone("+33123456782")
                .build();
        coach1 = coachRepository.save(c1);
        coach2 = coachRepository.save(c2);
    }

    @AfterEach
    void tearDown() {
        coachRepository.deleteAll();
    }

    @Test
    @DisplayName("Get all coaches as Admin")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void getCoaches_WithRoleAdmin() throws Exception {
        //Arrange
        var page = 0;
        var size = 10;
        var coach1BasePath = coachBasePath(0);
        var coach2BasePath = coachBasePath(1);
        //Act & Assert
        mockMvc.perform(get(BASE_URI).param("page", String.valueOf(page)).param("size", String.valueOf(size)))
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
                .andExpect(jsonPath("$._links.self.href").value(String.format("http://localhost/api/coaches?page=%d&size=%d", page, size)))

                // Verifying high-level templates
                .andExpect(templatesCreateCoach(coachBasePath()))

                // Vérification des coaches dans _embedded
                .andExpect(jsonPath("$._embedded.coaches.length()").value(2))

                // Coach 1
                .andExpect(coach1(coach1BasePath))
                .andExpect(templatesDeleteCoach(coach1BasePath))
                .andExpect(templatesUpdateCoach(coach1BasePath))

                // Verifying Coach 2
                .andExpect(coach2(coach2BasePath))
                .andExpect(templatesDeleteCoach(coach2BasePath))
                .andExpect(templatesUpdateCoach(coach2BasePath));
    }

    @Test
    @DisplayName("Get all coaches as User")
    @WithMockUser(username = "user", roles = {"NO_USER"})
    public void getCoaches_WithoutRoleAdmin() throws Exception {
        //Arrange
        var page = 0;
        var size = 10;
        var coach1BasePath = coachBasePath(0);
        var coach2BasePath = coachBasePath(1);
        //Act & Assert
        mockMvc.perform(get(BASE_URI).param("page", String.valueOf(page)).param("size", String.valueOf(size)))
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
                .andExpect(jsonPath("$._links.self.href").value(String.format("http://localhost/api/coaches?page=%d&size=%d", page, size)))

                // Verifying high-level templates
                .andExpect(jsonPath("$._templates.createCoach").doesNotExist())

                // Verifying coaches in _embedded
                .andExpect(jsonPath("$._embedded.coaches.length()").value(2))

                // Coach 1
                .andExpect(coach1(coach1BasePath))
                .andExpect(jsonPath(coachBasePath(0) + "._templates.deleteCoach").doesNotExist())
                .andExpect(jsonPath(coachBasePath(0) + "._templates.updateCoach").doesNotExist())

                //Coach 2
                .andExpect(coach2(coach2BasePath))
                .andExpect(jsonPath(coachBasePath(1) + "._templates.deleteCoach").doesNotExist())
                .andExpect(jsonPath(coachBasePath(1) + "._templates.updateCoach").doesNotExist());
    }

    @Test
    @DisplayName("Get all coaches without authentication")
    public void getCoaches_WithoutAuthentication() throws Exception {
        //Arrange
        var page = 0;
        var size = 10;
        var coach1BasePath = coachBasePath(0);
        var coach2BasePath = coachBasePath(1);

        //Act & Assert
        mockMvc.perform(get(BASE_URI).param("page", String.valueOf(page)).param("size", String.valueOf(size)))
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
                .andExpect(jsonPath("$._links.self.href").value(String.format("http://localhost/api/coaches?page=%d&size=%d", page, size)))

                // Vérification des templates haut niveau
                .andExpect(jsonPath("$._templates.createCoach").doesNotExist())

                // Vérification des coaches dans _embedded
                .andExpect(jsonPath("$._embedded.coaches.length()").value(2))

                // Verifying Coach 1
                .andExpect(coach1(coach1BasePath))
                .andExpect(jsonPath(coachBasePath(0) + "._templates.deleteCoach").doesNotExist())
                .andExpect(jsonPath(coachBasePath(0) + "._templates.updateCoach").doesNotExist())

                //Coach 2
                .andExpect(coach2(coach2BasePath))
                .andExpect(jsonPath(coachBasePath(1) + "._templates.deleteCoach").doesNotExist())
                .andExpect(jsonPath(coachBasePath(1) + "._templates.updateCoach").doesNotExist());
    }

    @Test
    @DisplayName("Get coach by ID as Admin")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void getCoachById_WithRoleAdmin() throws Exception {
        //Arrange
        var page = 0;
        var size = 10;
        var coachBasePath = coachBasePath();
        //Act & Assert
        mockMvc.perform(get(BASE_URI + "/" + coach1.getId()).param("page", String.valueOf(page)).param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/prs.hal-forms+json"))

                // Coach 1
                .andExpect(coach1(coachBasePath))
                .andExpect(templatesDeleteCoach(coachBasePath))
                .andExpect(templatesUpdateCoach(coachBasePath));
    }

    @Test
    @DisplayName("Get coach by ID as User")
    @WithMockUser(username = "user", roles = {"NO_USER"})
    public void getCoachById_WithoutRoleAdmin() throws Exception {
        //Arrange
        var page = 0;
        var size = 10;
        var coachBasePath = coachBasePath();
        //Act & Assert
        mockMvc.perform(get(BASE_URI + "/" + coach1.getId()).param("page", String.valueOf(page)).param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/prs.hal-forms+json"))

                // Coach 1
                .andExpect(coach1(coachBasePath))
                .andExpect(jsonPath(coachBasePath() + "._templates.deleteCoach").doesNotExist())
                .andExpect(jsonPath(coachBasePath() + "._templates.updateCoach").doesNotExist());
    }

    @Test
    @DisplayName("Get coach by ID without authentication")
    public void getCoachById_WithoutAuthentication() throws Exception {
        //Arrange
        var page = 0;
        var size = 10;
        var coachBasePath = coachBasePath();
        //Act & Assert
        mockMvc.perform(get(BASE_URI + "/" + coach1.getId()).param("page", String.valueOf(page)).param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/prs.hal-forms+json"))

                // Coach 1
                .andExpect(coach1(coachBasePath))
                .andExpect(jsonPath(coachBasePath() + "._templates.deleteCoach").doesNotExist())
                .andExpect(jsonPath(coachBasePath() + "._templates.updateCoach").doesNotExist());
    }


    @Test
    @DisplayName("Get coach by ID when it doesn't exist")
    public void getCoachById_WhenCoachOdDoesNotExist_ShouldReturn404() throws Exception {
        //Arrange
        var page = 0;
        var size = 10;
        var id = coach1.getId() + coach2.getId();  // Use ID that doesn't exist
        //Act & Assert
        mockMvc.perform(get(BASE_URI + "/" + id).param("page", String.valueOf(page)).param("size", String.valueOf(size)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/prs.hal-forms+json"));
    }

    @Test
    @DisplayName("Create a new coach with valid data as Admin")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void createCoach_WithValidCoach() throws Exception {
        //Arrange
        CoachDTORequest coachDTORequest = getCoachDTORequest();
        var coachBasePath = coachBasePath();
        //Act & Assert
        mockMvc.perform(post(BASE_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(coachDTORequest)))
                .andExpect(status().isCreated())
                .andExpect(coach(coachBasePath(), null, coachDTORequest.name(), coachDTORequest.surname(), coachDTORequest.email(), coachDTORequest.phone()))
                .andExpect(templatesDeleteCoach(coachBasePath))
                .andExpect(templatesUpdateCoach(coachBasePath));

        verifyNumberOfCoaches(3);
    }

    @Test
    @DisplayName("Create a new coach as User (Forbidden)")
    @WithMockUser(username = "user", roles = {"NO_USER"})
    public void createCoach_WithoutRoleAdmin() throws Exception {
        //Arrange
        CoachDTORequest coachDTORequest = getCoachDTORequest();
        //Act & Assert
        mockMvc.perform(post(BASE_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(coachDTORequest)))
                .andExpect(status().isForbidden());
        verifyNumberOfCoaches(2);
    }

    @Test
    @DisplayName("Create a new coach without authentication (Unauthorized)")
    public void createCoach_WithoutAuthentication() throws Exception {
        //Arrange
        CoachDTORequest coachDTORequest = getCoachDTORequest();
        //Act & Assert
        mockMvc.perform(post(BASE_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(coachDTORequest)))
                .andExpect(status().isUnauthorized());
        verifyNumberOfCoaches(2);
    }

    @Test
    @DisplayName("Create a new coach with invalid data (Bad Request)")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void createCoach_WhenArgumentNotValid() throws Exception {
        //Arrange
        CoachDTORequest coachDTORequest = CoachDTORequest.builder()
                .name("John3")
                .surname("Doe3")
                .email("john.doe") // bad email
                .phone("+33123456783")
                .build();
        //Act & Assert
        mockMvc.perform(post(BASE_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(coachDTORequest)))
                .andExpect(status().isBadRequest());
        verifyNumberOfCoaches(2);
    }

    private void verifyNumberOfCoaches(int n) throws Exception {
        mockMvc.perform(get(BASE_URI))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.coaches.length()").value(n));
    }

    @Test
    @DisplayName("Update a coach with valid data as Admin")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void updateCoach_Success_WithValidData() throws Exception {
        // Arrange : Préparation du DTO contenant les données valides
        CoachDTORequest updatedCoachDTO = getValidDTORequest();
        String updatedCoachDTOJson = this.objectMapper.writeValueAsString(updatedCoachDTO);
        Long existingCoachId = coach1.getId(); // An existing coach preconfigured in setUp()
        var coachBasePath = coachBasePath();
        // Act & Assert: Sending the PUT request with assertions on the response
        this.mockMvc.perform(put(BASE_URI + "/" + existingCoachId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedCoachDTOJson))
                .andExpect(status().isOk()) // Verifies a 200 status
                .andExpect(coach(coachBasePath(), existingCoachId, updatedCoachDTO.name(), updatedCoachDTO.surname(), updatedCoachDTO.email(), updatedCoachDTO.phone()))
                .andExpect(templatesDeleteCoach(coachBasePath))
                .andExpect(templatesUpdateCoach(coachBasePath));
        this.mockMvc.perform(get(BASE_URI + "/" + existingCoachId))
                .andExpect(status().isOk())
                .andExpect(coach(coachBasePath(), existingCoachId, updatedCoachDTO.name(), updatedCoachDTO.surname(), updatedCoachDTO.email(), updatedCoachDTO.phone()))
                .andExpect(templatesDeleteCoach(coachBasePath))
                .andExpect(templatesUpdateCoach(coachBasePath));
        verifyIfCoachExistWithId(coach2.getId(), coachBasePath());
    }

    @Test
    @DisplayName("Update a coach as User (Forbidden)")
    @WithMockUser(username = "user", roles = {"NO_USER"})
    public void updateCoach_Fails_WithoutRoleAdmin() throws Exception {
        // Arrange : Préparation du DTO contenant les données valides
        CoachDTORequest updatedCoachDTO = getValidDTORequest();
        String updatedCoachDTOJson = this.objectMapper.writeValueAsString(updatedCoachDTO);
        Long existingCoachId = coach1.getId(); // Une salle existante préconfigurée dans setUp()

        // Act & Assert: Sending the PUT request with assertions on the response (FORBIDDEN)
        this.mockMvc.perform(put(BASE_URI + "/" + existingCoachId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedCoachDTOJson))
                .andExpect(status().isForbidden()); // Verifies a 403 status for unauthorized role
        verifyIfCoachExistWithId(existingCoachId, coachBasePath());
    }

    @Test
    @DisplayName("Update a coach without authentication (Unauthorized)")
    public void updateCoach_Fails_WithoutAuthentication() throws Exception {
        // Arrange: Preparing the DTO containing valid data
        CoachDTORequest updatedCoachDTO = getValidDTORequest();
        String updatedCoachDTOJson = this.objectMapper.writeValueAsString(updatedCoachDTO);
        Long existingCoachId = coach1.getId(); // Une salle existante préconfigurée dans setUp()

        // Act & Assert : Envoie de la requête PUT avec des assertions sur la réponse (UNAUTHORIZED)
        this.mockMvc.perform(put(BASE_URI + "/" + existingCoachId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedCoachDTOJson))
                .andExpect(status().isUnauthorized()); // Vérifie un statut 401 pour utilisateur non authentifié
        verifyIfCoachExistWithId(existingCoachId, coachBasePath());
    }

    private static CoachDTORequest getValidDTORequest() {
        return CoachDTORequest.builder()
                .name("UpdateJohn")
                .surname("UpdateDoe")
                .email("Updatejohn.doe@example.com")
                .phone("+33123456784")
                .build();
    }

    @Test
    @DisplayName("Update a coach with invalid data (Bad Request)")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void updateCoach_Fails_WithInvalidData() throws Exception {
        // Arrange : Création d'un DTO avec des données non valides (par exemple, sans nom)
        CoachDTORequest invalidCoachDTO = CoachDTORequest.builder()
                .name("UpdateJohn")
                .surname("UpdateDoe")
                .email("Updatejo") //email doesn't valid
                .phone("+3312000003")
                .build();
        String invalidCoachDTOJson = this.objectMapper.writeValueAsString(invalidCoachDTO);
        Long existingCoachId = coach1.getId(); // Une salle existante préconfigurée dans setUp()

        // Act & Assert: Sending the PUT request with assertions on the response (BAD_REQUEST)
        this.mockMvc.perform(put(BASE_URI + "/" + existingCoachId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidCoachDTOJson))
                .andExpect(status().isBadRequest()); // Verifies a 400 status for invalid data
        verifyIfCoachExistWithId(existingCoachId, coachBasePath());
    }

    @Test
    @DisplayName("Delete a coach as Admin")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void deleteCoach_Success_WithRoleAdmin() throws Exception {
        // Arrange : ID d'un Coach existant (préconfiguré dans setUp())
        Long existingCoachId = coach1.getId();

        // Act & Assert: Successful deletion
        this.mockMvc.perform(delete(BASE_URI + "/" + existingCoachId))
                .andExpect(status().isNoContent()); // Vérifie un statut 204 No Content
        this.mockMvc.perform(get(BASE_URI + "/" + existingCoachId))
                .andExpect(status().isNotFound());
        verifyIfCoachExistWithId(coach2.getId(), coachBasePath());
    }

    @Test
    @DisplayName("Delete a coach as User (Forbidden)")
    @WithMockUser(username = "user", roles = {"NO_USER"})
    public void deleteCoach_Fails_WithoutRoleAdmin() throws Exception {
        // Arrange : ID d'un Coach existant (préconfiguré dans setUp())
        Long existingCoachId = coach1.getId();

        // Act & Assert: Attempted deletion by a non-admin user
        this.mockMvc.perform(delete(BASE_URI + "/" + existingCoachId))
                .andExpect(status().isForbidden()); // Vérifie un statut 403 Forbidden
        verifyIfCoachExistWithId(existingCoachId, coachBasePath());
    }

    @Test
    @DisplayName("Delete a coach without authentication (Unauthorized)")
    public void deleteCoach_Fails_WithoutAuthentication() throws Exception {
        // Arrange: ID of an existing coach (preconfigured in setUp())
        Long existingCoachId = coach1.getId();

        // Act & Assert: Tentative de suppression sans authentification
        this.mockMvc.perform(delete(BASE_URI + "/" + existingCoachId))
                .andExpect(status().isUnauthorized()); // Vérifie un statut 401 Unauthorized
        verifyIfCoachExistWithId(existingCoachId, coachBasePath());
    }


    @Test
    @DisplayName("Delete a coach when it doesn't exist")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void deleteCoach_Fails_WhenCoachDoesNotExist() throws Exception {
        // Arrange: ID d'un Coach inexistant
        long nonExistentCoachId = coach1.getId() + coach2.getId();

        // Act & Assert: Attempted deletion when the coach ID does not exist
        this.mockMvc.perform(delete(BASE_URI + "/" + nonExistentCoachId))
                .andExpect(status().isNotFound()); // Vérifie un statut 404 Not Found
    }


    private void verifyIfCoachExistWithId(long existingCoachId, String coachBasePath) throws Exception {
        coachBasePath = (coachBasePath == null) ? coachBasePath() : coachBasePath;
        if (existingCoachId == coach1.getId()) {
            this.mockMvc.perform(get(BASE_URI + "/" + existingCoachId))
                    .andExpect(status().isOk())
                    .andExpect(coach1(coachBasePath));
        } else if (existingCoachId == coach2.getId()) {
            this.mockMvc.perform(get(BASE_URI + "/" + existingCoachId))
                    .andExpect(status().isOk())
                    .andExpect(coach2(coachBasePath));
        } else {
            throw new Exception("The coach ID does not exist");
        }

    }

    private static CoachDTORequest getCoachDTORequest() {
        return CoachDTORequest.builder()
                .name("John3")
                .surname("Doe3")
                .email("john.doe@example.com3")
                .phone("+33123456783")
                .build();
    }

    private String coachBasePath(int index) {
        return String.format("$._embedded.coaches[%d]", index);
    }

    private String coachBasePath() {
        return "$";
    }


    private ResultMatcher coach(String coachBasePath, Long id, String name, String surname, String email, String phone) {
        return resultActions -> {
            if (id != null) {
                jsonPath(coachBasePath + ".id").value(id).match(resultActions);
                jsonPath(coachBasePath + "._links.self.href").value("http://localhost/api/coaches/" + id).match(resultActions);
            }

            jsonPath(coachBasePath + ".name").value(name).match(resultActions);
            jsonPath(coachBasePath + ".surname").value(surname).match(resultActions);
            jsonPath(coachBasePath + ".email").value(email).match(resultActions);
            jsonPath(coachBasePath + ".phone").value(phone).match(resultActions);
            jsonPath(coachBasePath + "._links.self.href").value(Matchers.matchesPattern("http://localhost/api/coaches/" + "\\d+")).match(resultActions);
            jsonPath(coachBasePath + "._links.coaches.href").value("http://localhost/api/coaches").match(resultActions);
        };
    }


    private ResultMatcher coach1(String coachBasePath) {
        return coach(coachBasePath, coach1.getId(), coach1.getName(), coach1.getSurname(), coach1.getEmail(), coach1.getPhone());
    }

    private ResultMatcher coach2(String coachBasePath) {
        return coach(coachBasePath, coach2.getId(), coach2.getName(), coach2.getSurname(), coach2.getEmail(), coach2.getPhone());
    }

    private ResultMatcher templatesDeleteCoach(String basePath) {
        var basePathTemplates = String.format("%s._templates.deleteCoach", basePath);
        return resultActions -> {
            jsonPath(basePathTemplates + ".method").value("DELETE").match(resultActions);
            jsonPath(basePathTemplates + ".properties").isEmpty().match(resultActions);
        };
    }

    private ResultMatcher templatesUpdateCoach(String basePath) {
        var basePathTemplates = String.format("%s._templates.updateCoach", basePath);
        return resultActions -> {
            jsonPath(basePathTemplates + ".method").value("PUT").match(resultActions);
            jsonPath(basePathTemplates + ".properties.length()").value(4).match(resultActions);
            jsonPath(basePathTemplates + ".properties[0].name").value("email").match(resultActions);
            jsonPath(basePathTemplates + ".properties[0].readOnly").value(true).match(resultActions);
            jsonPath(basePathTemplates + ".properties[0].required").value(true).match(resultActions);
            jsonPath(basePathTemplates + ".properties[0].type").value("email").match(resultActions);
            jsonPath(basePathTemplates + ".properties[1].name").value("name").match(resultActions);
            jsonPath(basePathTemplates + ".properties[1].regex").value("^(?=\\s*\\S).*$").match(resultActions);
            jsonPath(basePathTemplates + ".properties[1].readOnly").value(true).match(resultActions);
            jsonPath(basePathTemplates + ".properties[1].required").value(true).match(resultActions);
            jsonPath(basePathTemplates + ".properties[1].type").value("text").match(resultActions);
            jsonPath(basePathTemplates + ".properties[2].name").value("phone").match(resultActions);
            jsonPath(basePathTemplates + ".properties[2].regex").value("\\+?[0-9]{10,15}").match(resultActions);
            jsonPath(basePathTemplates + ".properties[2].readOnly").value(true).match(resultActions);
            jsonPath(basePathTemplates + ".properties[2].type").value("text").match(resultActions);
            jsonPath(basePathTemplates + ".properties[3].name").value("surname").match(resultActions);
            jsonPath(basePathTemplates + ".properties[3].regex").value("^(?=\\s*\\S).*$").match(resultActions);
            jsonPath(basePathTemplates + ".properties[3].readOnly").value(true).match(resultActions);
            jsonPath(basePathTemplates + ".properties[3].required").value(true).match(resultActions);
            jsonPath(basePathTemplates + ".properties[3].type").value("text").match(resultActions);
        };
    }

    private ResultMatcher templatesCreateCoach(String basePath) {
        var basePathTemplates = String.format("%s._templates.createCoach", basePath);
        return resultActions -> {
            jsonPath(basePathTemplates + ".method").value("POST").match(resultActions);
            jsonPath(basePathTemplates + ".target").value("http://localhost/api/coaches").match(resultActions);
            jsonPath(basePathTemplates + ".properties.length()").value(4).match(resultActions);
            jsonPath(basePathTemplates + ".properties[0].name").value("email").match(resultActions);
            jsonPath(basePathTemplates + ".properties[0].readOnly").value(true).match(resultActions);
            jsonPath(basePathTemplates + ".properties[0].required").value(true).match(resultActions);
            jsonPath(basePathTemplates + ".properties[0].type").value("email").match(resultActions);
            jsonPath(basePathTemplates + ".properties[1].name").value("name").match(resultActions);
            jsonPath(basePathTemplates + ".properties[1].regex").value("^(?=\\s*\\S).*$").match(resultActions);
            jsonPath(basePathTemplates + ".properties[1].readOnly").value(true).match(resultActions);
            jsonPath(basePathTemplates + ".properties[1].required").value(true).match(resultActions);
            jsonPath(basePathTemplates + ".properties[1].type").value("text").match(resultActions);
            jsonPath(basePathTemplates + ".properties[2].name").value("phone").match(resultActions);
            jsonPath(basePathTemplates + ".properties[2].regex").value("\\+?[0-9]{10,15}").match(resultActions);
            jsonPath(basePathTemplates + ".properties[2].readOnly").value(true).match(resultActions);
            jsonPath(basePathTemplates + ".properties[2].type").value("text").match(resultActions);
            jsonPath(basePathTemplates + ".properties[3].name").value("surname").match(resultActions);
            jsonPath(basePathTemplates + ".properties[3].regex").value("^(?=\\s*\\S).*$").match(resultActions);
            jsonPath(basePathTemplates + ".properties[3].readOnly").value(true).match(resultActions);
            jsonPath(basePathTemplates + ".properties[3].required").value(true).match(resultActions);
            jsonPath(basePathTemplates + ".properties[3].type").value("text").match(resultActions);
        };


    }
}

/*
{
  "_embedded": {
    "halls": [
      {
        "id": 12,
        "name": "John",
        "surname": "Doe",
        "email": "john.doe@example.com",
        "phone": "+33123456781",
        "_links": {
          "coaches": {
            "href": "http://localhost/api/coaches"
          },
          "self": {
            "href": "http://localhost/api/coaches/12"
          }
        },
        "_templates": {
          "default": {
            "method": "DELETE",
            "properties": []
          },
          "updateCoach": {
            "method": "PUT",
            "properties": [
              {
                "name": "email",
                "readOnly": true,
                "required": true,
                "type": "email"
              },
              {
                "name": "name",
                "regex": "^(?=\\s*\\S).*$",
                "readOnly": true,
                "required": true,
                "type": "text"
              },
              {
                "name": "phone",
                "regex": "\\+?[0-9]{10,15}",
                "readOnly": true,
                "type": "text"
              },
              {
                "name": "surname",
                "regex": "^(?=\\s*\\S).*$",
                "readOnly": true,
                "required": true,
                "type": "text"
              }
            ]
          },
          "deleteCoach": {
            "method": "DELETE",
            "properties": []
          }
        }
      },
      {
        "id": 13,
        "name": "John2",
        "surname": "Doe2",
        "email": "john.doe@example.com2",
        "phone": "+33123456782",
        "_links": {
          "coaches": {
            "href": "http://localhost/api/coaches"
          },
          "self": {
            "href": "http://localhost/api/coaches/13"
          }
        },
        "_templates": {
          "default": {
            "method": "DELETE",
            "properties": []
          },
          "updateCoach": {
            "method": "PUT",
            "properties": [
              {
                "name": "email",
                "readOnly": true,
                "required": true,
                "type": "email"
              },
              {
                "name": "name",
                "regex": "^(?=\\s*\\S).*$",
                "readOnly": true,
                "required": true,
                "type": "text"
              },
              {
                "name": "phone",
                "regex": "\\+?[0-9]{10,15}",
                "readOnly": true,
                "type": "text"
              },
              {
                "name": "surname",
                "regex": "^(?=\\s*\\S).*$",
                "readOnly": true,
                "required": true,
                "type": "text"
              }
            ]
          },
          "deleteCoach": {
            "method": "DELETE",
            "properties": []
          }
        }
      }
    ]
  },
  "_links": {
    "self": {
      "href": "http://localhost/api/coaches?page=0&size=10"
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
          "name": "email",
          "readOnly": true,
          "required": true,
          "type": "email"
        },
        {
          "name": "name",
          "regex": "^(?=\\s*\\S).*$",
          "readOnly": true,
          "required": true,
          "type": "text"
        },
        {
          "name": "phone",
          "regex": "\\+?[0-9]{10,15}",
          "readOnly": true,
          "type": "text"
        },
        {
          "name": "surname",
          "regex": "^(?=\\s*\\S).*$",
          "readOnly": true,
          "required": true,
          "type": "text"
        }
      ],
      "target": "http://localhost/api/coaches"
    },
    "createCoach": {
      "method": "POST",
      "properties": [
        {
          "name": "email",
          "readOnly": true,
          "required": true,
          "type": "email"
        },
        {
          "name": "name",
          "regex": "^(?=\\s*\\S).*$",
          "readOnly": true,
          "required": true,
          "type": "text"
        },
        {
          "name": "phone",
          "regex": "\\+?[0-9]{10,15}",
          "readOnly": true,
          "type": "text"
        },
        {
          "name": "surname",
          "regex": "^(?=\\s*\\S).*$",
          "readOnly": true,
          "required": true,
          "type": "text"
        }
      ],
      "target": "http://localhost/api/coaches"
    }
  }
}
*/
