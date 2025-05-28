package fr.hoenheimsports.contactservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.hoenheimsports.contactservice.dto.ApiErrorModel;
import fr.hoenheimsports.contactservice.dto.EmailRequest;
import fr.hoenheimsports.contactservice.service.EmailService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EmailController.class)
class EmailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmailService emailService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSendEmail() throws Exception {
        // Creation of a mock request
        EmailRequest emailRequest = new EmailRequest("test@example.com", "Test Name", "Test Message");

        // Mock configuration for the service
        doNothing().when(emailService).sendEmail(emailRequest);

        // Endpoint call and verifications
        mockMvc.perform(post("/sendEmail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailRequest)))
                .andExpect(status().isNoContent());

        // Vérification que le service a bien été appelé avec les bons paramètres
        Mockito.verify(emailService).sendEmail(emailRequest);
    }

    @Test
    void testSendEmail_EmailNull() throws Exception {
        // Creation of an invalid request (example: "email" is null)
        EmailRequest invalidEmailRequest = new EmailRequest(null, "Test Name", "Test Message");

        // Endpoint call with the invalid request
        mockMvc.perform(post("/sendEmail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidEmailRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(getValidationErrorInRequestPayload("email: The email field cannot be empty")); 
    }



    @Test
    void testSendEmail_NameNull() throws Exception {
        // Request with a null "name"
        EmailRequest invalidRequest = new EmailRequest("test@example.com", null, "Ceci est un message valide.");

        mockMvc.perform(post("/sendEmail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(getValidationErrorInRequestPayload("name: The name field cannot be empty"));
    }

    @Test
    void testSendEmail_MessageNull() throws Exception {
        // Request with a null "message"
        EmailRequest invalidRequest = new EmailRequest("test@example.com", "Valid Name", null);

        mockMvc.perform(post("/sendEmail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(getValidationErrorInRequestPayload("message: The message field cannot be empty"));
    }


    @Test
    void testSendEmail_NameTooShort() throws Exception {
        EmailRequest invalidRequest = new EmailRequest("test@example.com", "Jo", "Ceci est un message valide.");

        mockMvc.perform(post("/sendEmail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(getValidationErrorInRequestPayload("name: The name must be between 3 and 50 characters"));
    }

    @Test
    void testSendEmail_NameTooLong() throws Exception {
        EmailRequest invalidRequest = new EmailRequest("test@example.com", "J".repeat(51), "Ceci est un message valide.");

        mockMvc.perform(post("/sendEmail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(getValidationErrorInRequestPayload("name: The name must be between 3 and 50 characters"));
    }

    @Test
    void testSendEmail_EmailInvalid() throws Exception {
        EmailRequest invalidRequest = new EmailRequest("not-an-email", "Valid Name", "Ceci est un message valide.");

        mockMvc.perform(post("/sendEmail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(getValidationErrorInRequestPayload("email: The email address is not valid"));
    }

    @Test
    void testSendEmail_MessageTooShort() throws Exception {
        EmailRequest invalidRequest = new EmailRequest("test@example.com", "Valid Name", "Short");

        mockMvc.perform(post("/sendEmail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(getValidationErrorInRequestPayload("message: The message must be between 10 and 1000 characters"));
    }

    @Test
    void testSendEmail_MessageTooLong() throws Exception {
        EmailRequest invalidRequest = new EmailRequest("test@example.com", "Valid Name", "Lorem ipsum ".repeat(101));

        mockMvc.perform(post("/sendEmail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(getValidationErrorInRequestPayload("message: The message must be between 10 and 1000 characters"));
    }

    @Test
    void testSendEmail_NameNotBlank() throws Exception {
        // Request with a blank "name"
        EmailRequest invalidRequest = new EmailRequest("test@example.com", "", "Ceci est un message valide.");

        mockMvc.perform(post("/sendEmail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(getValidationErrorInRequestPayload("name: The name field cannot be empty"));
    }

    @Test
    void testSendEmail_NameOnlyWhitespace() throws Exception {
        // Requête avec un "name" contenant uniquement des espaces
        EmailRequest invalidRequest = new EmailRequest("test@example.com", "    ", "Ceci est un message valide.");

        mockMvc.perform(post("/sendEmail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(getValidationErrorInRequestPayload("name: The name field cannot be empty"));
    }

    @Test
    void testSendEmail_MessageNotBlank() throws Exception {
        // Request with a blank "message"
        EmailRequest invalidRequest = new EmailRequest("test@example.com", "Valid Name", "");

        mockMvc.perform(post("/sendEmail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(getValidationErrorInRequestPayload("message: The message field cannot be empty"));
    }

    @Test
    void testSendEmail_MessageOnlyWhitespace() throws Exception {
        // Requête avec un "message" contenant uniquement des espaces
        EmailRequest invalidRequest = new EmailRequest("test@example.com", "Valid Name", "     ");

        mockMvc.perform(post("/sendEmail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(getValidationErrorInRequestPayload("message: The message field cannot be empty"));
    }

    private ResultMatcher getValidationErrorInRequestPayload(String detail) {
        return result -> {
            String responseContent = result.getResponse().getContentAsString();
            ApiErrorModel apiError = objectMapper.readValue(responseContent, ApiErrorModel.class);

            // Assertions avec AssertJ
            assertThat(apiError)
                    .isNotNull()
                    .satisfies(error -> {
                        assertThat(error.getType()).isEqualTo("https://www.hoenheimsports.fr/problem/validation-error");
                        assertThat(error.getTitle()).isEqualTo("Validation error in request payload");
                        assertThat(error.getStatus()).isEqualTo(400);
                        assertThat(error.getDetail()).contains(detail);
                        assertThat(error.getInstance()).isEqualTo("/sendEmail");
                    });

        };
    }
}