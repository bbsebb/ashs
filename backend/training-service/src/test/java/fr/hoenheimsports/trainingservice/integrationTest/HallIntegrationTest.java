package fr.hoenheimsports.trainingservice.integrationTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.hoenheimsports.trainingservice.TestcontainersConfiguration;
import fr.hoenheimsports.trainingservice.config.TestSecurityConfig;
import fr.hoenheimsports.trainingservice.dto.request.AddressDTORequest;
import fr.hoenheimsports.trainingservice.dto.request.HallDTOCreateRequest;
import fr.hoenheimsports.trainingservice.model.Address;
import fr.hoenheimsports.trainingservice.model.Hall;
import fr.hoenheimsports.trainingservice.repository.HallRepository;
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
public class HallIntegrationTest {

    private static final String BASE_URI = "/api/halls";

    @Autowired
    private HallRepository hallRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private Hall hall1 = new Hall();
    private Hall hall2 = new Hall();

    @BeforeEach
    void setUp() {
        var h1 = Hall.builder()
                .name("Gymnase")
                .address(
                        Address.builder()
                                .city("Hoenheim")
                                .country("France")
                                .street("rue des vosges")
                                .postalCode("67800")
                                .build())
                .build();
        var h2 = Hall.builder()
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
        hall1 = hallRepository.save(h1);
        hall2 = hallRepository.save(h2);
    }

    @AfterEach
    void tearDown() {
        hallRepository.deleteAll();
    }

    @Test
    @DisplayName("Get all halls as Admin")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void getHalls_WithRoleAdmin() throws Exception {
        //Arrange
        var page = 0;
        var size = 10;
        var hall1BasePath = hallBasePath(0);
        var hall2BasePath = hallBasePath(1);
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
                .andExpect(jsonPath("$._links.self.href").value(String.format("http://localhost/api/halls?page=%d&size=%d", page, size)))

                // Verifying high-level templates
                .andExpect(templatesCreateHall(hallBasePath()))

                // Vérification des halls dans _embedded
                .andExpect(jsonPath("$._embedded.halls.length()").value(2))

                // Hall 1
                .andExpect(hall1(hall1BasePath))
                .andExpect(templatesDeleteHall(hall1BasePath))
                .andExpect(templatesUpdateHall(hall1BasePath))

                // Verifying Hall 2
                .andExpect(hall2(hall2BasePath))
                .andExpect(templatesDeleteHall(hall2BasePath))
                .andExpect(templatesUpdateHall(hall2BasePath));
    }

    @Test
    @DisplayName("Get all halls as User")
    @WithMockUser(username = "user", roles = {"NO_NO_USER"})
    public void getHalls_WithoutRoleAdmin() throws Exception {
        //Arrange
        var page = 0;
        var size = 10;
        var hall1BasePath = hallBasePath(0);
        var hall2BasePath = hallBasePath(1);
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
                .andExpect(jsonPath("$._links.self.href").value(String.format("http://localhost/api/halls?page=%d&size=%d", page, size)))

                // Verifying high-level templates
                .andExpect(jsonPath("$._templates.createHall").doesNotExist())

                // Verifying halls in _embedded
                .andExpect(jsonPath("$._embedded.halls.length()").value(2))

                // Hall 1
                .andExpect(hall1(hall1BasePath))
                .andExpect(jsonPath(hallBasePath(0) + "._templates.deleteHall").doesNotExist())
                .andExpect(jsonPath(hallBasePath(0) + "._templates.updateHall").doesNotExist())

                //Hall 2
                .andExpect(hall2(hall2BasePath))
                .andExpect(jsonPath(hallBasePath(1) + "._templates.deleteHall").doesNotExist())
                .andExpect(jsonPath(hallBasePath(1) + "._templates.updateHall").doesNotExist());
    }

    @Test
    @DisplayName("Get all halls without authentication")
    public void getHalls_WithoutAuthentication() throws Exception {
        //Arrange
        var page = 0;
        var size = 10;
        var hall1BasePath = hallBasePath(0);
        var hall2BasePath = hallBasePath(1);

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
                .andExpect(jsonPath("$._links.self.href").value(String.format("http://localhost/api/halls?page=%d&size=%d", page, size)))

                // Vérification des templates haut niveau
                .andExpect(jsonPath("$._templates.createHall").doesNotExist())

                // Vérification des halls dans _embedded
                .andExpect(jsonPath("$._embedded.halls.length()").value(2))

                // Verifying Hall 1
                .andExpect(hall1(hall1BasePath))
                .andExpect(jsonPath(hallBasePath(0) + "._templates.deleteHall").doesNotExist())
                .andExpect(jsonPath(hallBasePath(0) + "._templates.updateHall").doesNotExist())

                //Hall 2
                .andExpect(hall2(hall2BasePath))
                .andExpect(jsonPath(hallBasePath(1) + "._templates.deleteHall").doesNotExist())
                .andExpect(jsonPath(hallBasePath(1) + "._templates.updateHall").doesNotExist());
    }

    @Test
    @DisplayName("Get hall by ID as Admin")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void getHallById_WithRoleAdmin() throws Exception {
        //Arrange
        var page = 0;
        var size = 10;
        var hallBasePath = hallBasePath();
        //Act & Assert
        mockMvc.perform(get(BASE_URI + "/" + hall1.getId()).param("page", String.valueOf(page)).param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/prs.hal-forms+json"))

                // Hall 1
                .andExpect(hall1(hallBasePath))
                .andExpect(templatesDeleteHall(hallBasePath))
                .andExpect(templatesUpdateHall(hallBasePath));
    }

    @Test
    @DisplayName("Get hall by ID as User")
    @WithMockUser(username = "user", roles = {"NO_USER"})
    public void getHallById_WithoutRoleAdmin() throws Exception {
        //Arrange
        var page = 0;
        var size = 10;
        var hallBasePath = hallBasePath();
        //Act & Assert
        mockMvc.perform(get(BASE_URI + "/" + hall1.getId()).param("page", String.valueOf(page)).param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/prs.hal-forms+json"))

                // Hall 1
                .andExpect(hall1(hallBasePath))
                .andExpect(jsonPath(hallBasePath() + "._templates.deleteHall").doesNotExist())
                .andExpect(jsonPath(hallBasePath() + "._templates.updateHall").doesNotExist());
    }

    @Test
    @DisplayName("Get hall by ID without authentication")
    public void getHallById_WithoutAuthentication() throws Exception {
        //Arrange
        var page = 0;
        var size = 10;
        var hallBasePath = hallBasePath();
        //Act & Assert
        mockMvc.perform(get(BASE_URI + "/" + hall1.getId()).param("page", String.valueOf(page)).param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/prs.hal-forms+json"))

                // Hall 1
                .andExpect(hall1(hallBasePath))
                .andExpect(jsonPath(hallBasePath() + "._templates.deleteHall").doesNotExist())
                .andExpect(jsonPath(hallBasePath() + "._templates.updateHall").doesNotExist());
    }


    @Test
    @DisplayName("Get hall by ID when it doesn't exist")
    public void getHallById_WhenHallOdDoesNotExist_ShouldReturn404() throws Exception {
        //Arrange
        var page = 0;
        var size = 10;
        var id = hall1.getId() + hall2.getId();  // Use ID that doesn't exist
        //Act & Assert
        mockMvc.perform(get(BASE_URI + "/" + id).param("page", String.valueOf(page)).param("size", String.valueOf(size)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/prs.hal-forms+json"));
    }

    @Test
    @DisplayName("Create a new hall with valid data as Admin")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void createHall_WithValidHall() throws Exception {
        //Arrange
        HallDTOCreateRequest hallDTOCreateRequest = getHallDTORequest();
        var hallBasePath = hallBasePath();
        //Act & Assert
        mockMvc.perform(post(BASE_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hallDTOCreateRequest)))
                .andExpect(status().isCreated())
                .andExpect(hall(hallBasePath(), null, hallDTOCreateRequest.name(), hallDTOCreateRequest.address().city(), hallDTOCreateRequest.address().country(), hallDTOCreateRequest.address().postalCode(), hallDTOCreateRequest.address().street()))
                .andExpect(templatesDeleteHall(hallBasePath))
                .andExpect(templatesUpdateHall(hallBasePath));

        verifyNumberOfHalls(3);
    }

    @Test
    @DisplayName("Create a new hall as User (Forbidden)")
    @WithMockUser(username = "user", roles = {"NO_USER"})
    public void createHall_WithoutRoleAdmin() throws Exception {
        //Arrange
        HallDTOCreateRequest hallDTOCreateRequest = getHallDTORequest();
        //Act & Assert
        mockMvc.perform(post(BASE_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hallDTOCreateRequest)))
                .andExpect(status().isForbidden());
        verifyNumberOfHalls(2);
    }

    @Test
    @DisplayName("Create a new hall without authentication (Unauthorized)")
    public void createHall_WithoutAuthentication() throws Exception {
        //Arrange
        HallDTOCreateRequest hallDTOCreateRequest = getHallDTORequest();
        //Act & Assert
        mockMvc.perform(post(BASE_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hallDTOCreateRequest)))
                .andExpect(status().isUnauthorized());
        verifyNumberOfHalls(2);
    }

    @Test
    @DisplayName("Create a new hall with invalid data (Bad Request)")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void createHall_WhenArgumentNotValid() throws Exception {
        //Arrange
        HallDTOCreateRequest hallDTOCreateRequest = HallDTOCreateRequest.builder()
                .address(
                        AddressDTORequest.builder()
                                .city("Hoenheim3")
                                .country("France3")
                                .street("rue des vosges3")
                                .postalCode("67803")
                                .build()
                )
                .build();
        //Act & Assert
        mockMvc.perform(post(BASE_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hallDTOCreateRequest)))
                .andExpect(status().isBadRequest());
        verifyNumberOfHalls(2);
    }

    private void verifyNumberOfHalls(int n) throws Exception {
        mockMvc.perform(get(BASE_URI))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.halls.length()").value(n));
    }

    @Test
    @DisplayName("Update a hall with valid data as Admin")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void updateHall_Success_WithValidData() throws Exception {
        // Arrange : Préparation du DTO contenant les données valides
        HallDTOCreateRequest updatedHallDTO = getValidDTORequest();
        String updatedHallDTOJson = this.objectMapper.writeValueAsString(updatedHallDTO);
        Long existingHallId = hall1.getId(); // An existing hall preconfigured in setUp()
        var hallBasePath = hallBasePath();
        // Act & Assert: Sending the PUT request with assertions on the response
        this.mockMvc.perform(put(BASE_URI + "/" + existingHallId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedHallDTOJson))
                .andExpect(status().isOk()) // Verifies a 200 status
                .andExpect(hall(hallBasePath(), existingHallId, updatedHallDTO.name(), updatedHallDTO.address().city(), updatedHallDTO.address().country(), updatedHallDTO.address().postalCode(), updatedHallDTO.address().street()))
                .andExpect(templatesDeleteHall(hallBasePath))
                .andExpect(templatesUpdateHall(hallBasePath));
        this.mockMvc.perform(get(BASE_URI + "/" + existingHallId))
                .andExpect(status().isOk())
                .andExpect(hall(hallBasePath(), existingHallId, updatedHallDTO.name(), updatedHallDTO.address().city(), updatedHallDTO.address().country(), updatedHallDTO.address().postalCode(), updatedHallDTO.address().street()))
                .andExpect(templatesDeleteHall(hallBasePath))
                .andExpect(templatesUpdateHall(hallBasePath));
        verifyIfHallExistWithId(hall2.getId(), hallBasePath());
    }

    @Test
    @DisplayName("Update a hall as User (Forbidden)")
    @WithMockUser(username = "user", roles = {"NO_USER"})
    public void updateHall_Fails_WithoutRoleAdmin() throws Exception {
        // Arrange : Préparation du DTO contenant les données valides
        HallDTOCreateRequest updatedHallDTO = getValidDTORequest();
        String updatedHallDTOJson = this.objectMapper.writeValueAsString(updatedHallDTO);
        Long existingHallId = hall1.getId(); // Une salle existante préconfigurée dans setUp()

        // Act & Assert: Sending the PUT request with assertions on the response (FORBIDDEN)
        this.mockMvc.perform(put(BASE_URI + "/" + existingHallId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedHallDTOJson))
                .andExpect(status().isForbidden()); // Verifies a 403 status for unauthorized role
        verifyIfHallExistWithId(existingHallId, hallBasePath());
    }

    private static HallDTOCreateRequest getValidDTORequest() {
        return HallDTOCreateRequest.builder()
                .name("Updated Hall Name")
                .address(AddressDTORequest.builder()
                        .city("Updated City")
                        .street("Updated Street")
                        .country("Updated Country")
                        .postalCode("12345")
                        .build())
                .build();
    }

    @Test
    @DisplayName("Update a hall without authentication (Unauthorized)")
    public void updateHall_Fails_WithoutAuthentication() throws Exception {
        // Arrange: Preparing the DTO containing valid data
        HallDTOCreateRequest updatedHallDTO = getValidDTORequest();
        String updatedHallDTOJson = this.objectMapper.writeValueAsString(updatedHallDTO);
        Long existingHallId = hall1.getId(); // Une salle existante préconfigurée dans setUp()

        // Act & Assert : Envoie de la requête PUT avec des assertions sur la réponse (UNAUTHORIZED)
        this.mockMvc.perform(put(BASE_URI + "/" + existingHallId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedHallDTOJson))
                .andExpect(status().isUnauthorized()); // Vérifie un statut 401 pour utilisateur non authentifié
        verifyIfHallExistWithId(existingHallId, hallBasePath());
    }

    @Test
    @DisplayName("Update a hall with invalid data (Bad Request)")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void updateHall_Fails_WithInvalidData() throws Exception {
        // Arrange : Création d'un DTO avec des données non valides (par exemple, sans nom)
        HallDTOCreateRequest invalidHallDTO = HallDTOCreateRequest.builder()
                .name("Updated Hall Name")
                .address(AddressDTORequest.builder()
                        .city("Updated City")
                        .street("Updated Street")
                        .country("Updated Country")
                        .postalCode("123456") // Le dto n'accepte que 5 chiffre
                        .build())
                .build();
        String invalidHallDTOJson = this.objectMapper.writeValueAsString(invalidHallDTO);
        Long existingHallId = hall1.getId(); // Une salle existante préconfigurée dans setUp()

        // Act & Assert: Sending the PUT request with assertions on the response (BAD_REQUEST)
        this.mockMvc.perform(put(BASE_URI + "/" + existingHallId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidHallDTOJson))
                .andExpect(status().isBadRequest()); // Verifies a 400 status for invalid data
        verifyIfHallExistWithId(existingHallId, hallBasePath());
    }

    @Test
    @DisplayName("Delete a hall as Admin")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void deleteHall_Success_WithRoleAdmin() throws Exception {
        // Arrange : ID d'un Hall existant (préconfiguré dans setUp())
        Long existingHallId = hall1.getId();

        // Act & Assert: Successful deletion
        this.mockMvc.perform(delete(BASE_URI + "/" + existingHallId))
                .andExpect(status().isNoContent()); // Vérifie un statut 204 No Content
        this.mockMvc.perform(get(BASE_URI + "/" + existingHallId))
                .andExpect(status().isNotFound());
        verifyIfHallExistWithId(hall2.getId(), hallBasePath());
    }

    @Test
    @DisplayName("Delete a hall as User (Forbidden)")
    @WithMockUser(username = "user", roles = {"NO_USER"})
    public void deleteHall_Fails_WithoutRoleAdmin() throws Exception {
        // Arrange : ID d'un Hall existant (préconfiguré dans setUp())
        Long existingHallId = hall1.getId();

        // Act & Assert: Attempted deletion by a non-admin user
        this.mockMvc.perform(delete(BASE_URI + "/" + existingHallId))
                .andExpect(status().isForbidden()); // Vérifie un statut 403 Forbidden
        verifyIfHallExistWithId(existingHallId, hallBasePath());
    }

    @Test
    @DisplayName("Delete a hall without authentication (Unauthorized)")
    public void deleteHall_Fails_WithoutAuthentication() throws Exception {
        // Arrange: ID of an existing hall (preconfigured in setUp())
        Long existingHallId = hall1.getId();

        // Act & Assert: Tentative de suppression sans authentification
        this.mockMvc.perform(delete(BASE_URI + "/" + existingHallId))
                .andExpect(status().isUnauthorized()); // Vérifie un statut 401 Unauthorized
        verifyIfHallExistWithId(existingHallId, hallBasePath());
    }


    @Test
    @DisplayName("Delete a hall when it doesn't exist")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void deleteHall_Fails_WhenHallDoesNotExist() throws Exception {
        // Arrange: ID d'un Hall inexistant
        long nonExistentHallId = hall1.getId() + hall2.getId();

        // Act & Assert: Attempted deletion when the hall ID does not exist
        this.mockMvc.perform(delete(BASE_URI + "/" + nonExistentHallId))
                .andExpect(status().isNotFound()); // Vérifie un statut 404 Not Found
    }


    private void verifyIfHallExistWithId(long existingHallId, String hallBasePath) throws Exception {
        hallBasePath = (hallBasePath == null) ? hallBasePath() : hallBasePath;
        if (existingHallId == hall1.getId()) {
            this.mockMvc.perform(get(BASE_URI + "/" + existingHallId))
                    .andExpect(status().isOk())
                    .andExpect(hall1(hallBasePath));
        } else if (existingHallId == hall2.getId()) {
            this.mockMvc.perform(get(BASE_URI + "/" + existingHallId))
                    .andExpect(status().isOk())
                    .andExpect(hall2(hallBasePath));
        } else {
            throw new Exception("The hall ID does not exist");
        }

    }

    private static HallDTOCreateRequest getHallDTORequest() {
        return HallDTOCreateRequest.builder()
                .name("Gymnase3")
                .address(
                        AddressDTORequest.builder()
                                .city("Hoenheim3")
                                .country("France3")
                                .street("rue des vosges3")
                                .postalCode("67803")
                                .build()
                )
                .build();
    }

    private String hallBasePath(int index) {
        return String.format("$._embedded.halls[%d]", index);
    }

    private String hallBasePath() {
        return "$";
    }


    private ResultMatcher hall(String hallBasePath, Long id, String name, String city, String country, String postalCode, String street) {
        return resultActions -> {
            if (id != null) {
                jsonPath(hallBasePath + ".id").value(id).match(resultActions);
                jsonPath(hallBasePath + "._links.self.href").value("http://localhost/api/halls/" + id).match(resultActions);
            }

            jsonPath(hallBasePath + ".name").value(name).match(resultActions);
            jsonPath(hallBasePath + ".address.city").value(city).match(resultActions);
            jsonPath(hallBasePath + ".address.country").value(country).match(resultActions);
            jsonPath(hallBasePath + ".address.postalCode").value(postalCode).match(resultActions);
            jsonPath(hallBasePath + ".address.street").value(street).match(resultActions);
            jsonPath(hallBasePath + "._links.self.href").value(Matchers.matchesPattern("http://localhost/api/halls/" + "\\d+")).match(resultActions);
            jsonPath(hallBasePath + "._links.halls.href").value("http://localhost/api/halls").match(resultActions);
        };
    }


    private ResultMatcher hall1(String hallBasePath) {
        return hall(hallBasePath, hall1.getId(), hall1.getName(), hall1.getAddress().getCity(), hall1.getAddress().getCountry(), hall1.getAddress().getPostalCode(), hall1.getAddress().getStreet());
    }

    private ResultMatcher hall2(String hallBasePath) {
        return hall(hallBasePath, hall2.getId(), hall2.getName(), hall2.getAddress().getCity(), hall2.getAddress().getCountry(), hall2.getAddress().getPostalCode(), hall2.getAddress().getStreet());
    }

    private ResultMatcher templatesDeleteHall(String basePath) {
        var basePathTemplates = String.format("%s._templates.deleteHall", basePath);
        return resultActions -> {
            jsonPath(basePathTemplates + ".method").value("DELETE").match(resultActions);
            jsonPath(basePathTemplates + ".properties").isEmpty().match(resultActions);
        };
    }

    private ResultMatcher templatesUpdateHall(String basePath) {
        var basePathTemplates = String.format("%s._templates.updateHall", basePath);
        return resultActions -> {
            jsonPath(basePathTemplates + ".method").value("PUT").match(resultActions);
            jsonPath(basePathTemplates + ".properties.length()").value(2).match(resultActions);
            jsonPath(basePathTemplates + ".properties[0].name").value("address").match(resultActions);
            jsonPath(basePathTemplates + ".properties[1].name").value("name").match(resultActions);
            jsonPath(basePathTemplates + ".properties[1].max").value(50).match(resultActions);
            jsonPath(basePathTemplates + ".properties[1].min").value(0).match(resultActions);
            jsonPath(basePathTemplates + ".properties[1].regex").value("^(?=\\s*\\S).*$").match(resultActions);
            jsonPath(basePathTemplates + ".properties[1].type").value("range").match(resultActions);
        };
    }

    private ResultMatcher templatesCreateHall(String basePath) {
        var basePathTemplates = String.format("%s._templates.createHall", basePath);
        return resultActions -> {
            jsonPath(basePathTemplates + ".method").value("POST");
            jsonPath(basePathTemplates + ".target").value("http://localhost/api/halls").match(resultActions);
            jsonPath(basePathTemplates + ".properties[0].name").value("address").match(resultActions);
            jsonPath(basePathTemplates + ".properties[0].readOnly").value(true).match(resultActions);
            jsonPath(basePathTemplates + ".properties[0].required").value(true).match(resultActions);
            jsonPath(basePathTemplates + ".properties[1].name").value("name").match(resultActions);
            jsonPath(basePathTemplates + ".properties[1].readOnly").value(true).match(resultActions);
            jsonPath(basePathTemplates + ".properties[1].required").value(true).match(resultActions);
            jsonPath(basePathTemplates + ".properties[1].max").value(50).match(resultActions);
            jsonPath(basePathTemplates + ".properties[1].min").value(0).match(resultActions);
            jsonPath(basePathTemplates + ".properties[1].regex").value("^(?=\\s*\\S).*$").match(resultActions);
            jsonPath(basePathTemplates + ".properties[1].type").value("range").match(resultActions);
        };
    }




  /*
                {
                  "_embedded": {
                    "halls": [
                      {
                        "_links": {
                          "halls": {
                            "href": "http://localhost:55254/api/halls"
                          },
                          "self": {
                            "href": "http://localhost:55254/api/halls/1"
                          }
                        },
                        "_templates": {
                          "default": {
                            "method": "DELETE",
                            "properties": []
                          },
                          "deleteHall": {
                            "method": "DELETE",
                            "properties": []
                          },
                          "updateHall": {
                            "method": "PUT",
                            "properties": [
                              {
                                "name": "address",
                                "readOnly": true,
                                "required": true
                              },
                              {
                                "max": 50,
                                "min": 0,
                                "name": "name",
                                "readOnly": true,
                                "regex": "^(?=\\\\s*\\\\S).*$",
                                "required": true,
                                "type": "range"
                              }
                            ]
                          }
                        },
                        "address": {
                          "city": "Hoenheim",
                          "country": "France",
                          "postalCode": "67800",
                          "street": "rue des vosges"
                        },
                        "id": 1,
                        "name": "Gymnase"
                      },
                      {
                        "_links": {
                          "halls": {
                            "href": "http://localhost:55254/api/halls"
                          },
                          "self": {
                            "href": "http://localhost:55254/api/halls/2"
                          }
                        },
                        "_templates": {
                          "default": {
                            "method": "DELETE",
                            "properties": []
                          },
                          "deleteHall": {
                            "method": "DELETE",
                            "properties": []
                          },
                          "updateHall": {
                            "method": "PUT",
                            "properties": [
                              {
                                "name": "address",
                                "readOnly": true,
                                "required": true
                              },
                              {
                                "max": 50,
                                "min": 0,
                                "name": "name",
                                "readOnly": true,
                                "regex": "^(?=\\\\s*\\\\S).*$",
                                "required": true,
                                "type": "range"
                              }
                            ]
                          }
                        },
                        "address": {
                          "city": "Hoenheim2",
                          "country": "France2",
                          "postalCode": "67802",
                          "street": "rue des vosges2"
                        },
                        "id": 2,
                        "name": "Gymnase2"
                      }
                    ]
                  },
                  "_links": {
                    "self": {
                      "href": "http://localhost:55254/api/halls?page=0&size=20"
                    }
                  },
                  "_templates": {
                    "createHall": {
                      "method": "POST",
                      "properties": [
                        {
                          "name": "address",
                          "readOnly": true,
                          "required": true
                        },
                        {
                          "max": 50,
                          "min": 0,
                          "name": "name",
                          "readOnly": true,
                          "regex": "^(?=\\\\s*\\\\S).*$",
                          "required": true,
                          "type": "range"
                        }
                      ],
                      "target": "http://localhost:55254/api/halls"
                    },
                    "default": {
                      "method": "POST",
                      "properties": [
                        {
                          "name": "address",
                          "readOnly": true,
                          "required": true
                        },
                        {
                          "max": 50,
                          "min": 0,
                          "name": "name",
                          "readOnly": true,
                          "regex": "^(?=\\\\s*\\\\S).*$",
                          "required": true,
                          "type": "range"
                        }
                      ],
                      "target": "http://localhost:55254/api/halls"
                    }
                  },
                  "page": {
                    "number": 0,
                    "size": 20,
                    "totalElements": 2,
                    "totalPages": 1
                  }
                }
   */
}
