package fr.hoenheimsports.contactservice;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class ContactServiceIntegrationTest {

    @Autowired
    ObjectMapper objectMapper;

    @SuppressWarnings("resource")
    @Container
    private static final GenericContainer<?> mailhogContainer =
            new GenericContainer<>(DockerImageName.parse("mailhog/mailhog:latest"))
                    .withExposedPorts(1025, 8025); // 1025 = SMTP, 8025 = Web UI for email testing

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.mail.host", mailhogContainer::getHost);
        registry.add("spring.mail.port", () -> mailhogContainer.getMappedPort(1025));
        registry.add("spring.mail.properties.mail.smtp.from", () -> "contact@hoenheimsports.fr");
        registry.add("custom.contact.email", () -> "sebastien.burckhardt+test@hoenheimsports.fr");
    }

    @LocalServerPort
    private int port;

    HttpClient client;


    @BeforeEach
    void setUp() {

        // Initialize the HttpClient
        client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    @AfterEach
    void tearDown() {
        client.close();
    }

    @Test
            void contextLoads() {}

    @Test
    void shouldSendEmailViaController() throws IOException, InterruptedException {
        // ############# ARRANGE #############
        // Prepare data for the test
        String apiUrl = "http://localhost:" + port + "/sendEmail";
        String emailRequestJson = """
                {
                    "email": "john.doe@example.com",
                    "name": "John Doe",
                    "message": "This is a test message!"
                }
                """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(emailRequestJson))
                .build();

        // Prepare the URL for MailHog (to verify after sending the email)

        String mailhogApiUrl = "http://" + mailhogContainer.getHost() + ":" + mailhogContainer.getMappedPort(8025) + "/api/v2/messages";

        // ############# ACT #############
        // Send the POST request to the controller
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Récupérer les emails via l'API MailHog
        HttpRequest mailhogRequest = HttpRequest.newBuilder()
                .uri(URI.create(mailhogApiUrl))
                .GET()
                .build();

        HttpResponse<String> mailhogResponse = client.send(mailhogRequest, HttpResponse.BodyHandlers.ofString());



        // ############# ASSERT #############
        // Verify that the controller returns an HTTP 204 status
        assertThat(response.statusCode()).isEqualTo(204);


        MailHogResponse mailHogResponse = objectMapper.readValue(mailhogResponse.body(), MailHogResponse.class);

        // Vérification des e-mails capturés
        assertThat(mailHogResponse.total()).isGreaterThan(0);
        MailHogMessage message = mailHogResponse.items().getFirst();

        String encodedBody = message.content().body();



        // Valider le sujet de l'e-mail
        assertThat(message.content().headers().subject())
                .contains("Notification de formulaire de contact de : John Doe");

        // Valider le corps du message
        // Decode the body
        String decodedBody = QuotedPrintableDecoder.decodeQuotedPrintable(encodedBody);
        assertThat(decodedBody)
                .contains("Attention : ce message a été écrit par John Doe (john.doe@example.com).")
                .contains("Merci de ne pas cliquer sur 'Répondre' pour répondre directement à cet email.\n\n")
                .contains("This is a test message!");




    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    private record MailHogMessage(
            @JsonProperty("Content") MailHogContent content,  // Correspondance exacte avec "Content" dans le JSON
            @JsonProperty("To") List<MailHogRecipient> to    // Correspondance exacte avec "To"
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record MailHogContent(
            @JsonProperty("Body") String body,               // Correspondance exacte avec "Body"
            @JsonProperty("Headers") MailHogHeaders headers  // Correspondance exacte avec "Headers"
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record MailHogHeaders(
            @JsonProperty("Subject") List<String> subject    // Correspondance exacte avec "Subject"
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record MailHogRecipient(
            @JsonProperty("Mailbox") String mailbox,         // Correspondance exacte avec "Mailbox"
            @JsonProperty("Domain") String domain            // Correspondance exacte avec "Domain"
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record MailHogResponse(
            int total,
            @JsonProperty("items") List<MailHogMessage> items // Correspondance exacte avec "items"
    ) {}

    private static class QuotedPrintableDecoder {
        public static String decodeQuotedPrintable(String encoded) {
            try {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                byte[] data = encoded.getBytes(StandardCharsets.ISO_8859_1);

                for (int i = 0; i < data.length; i++) {
                    if (data[i] == '=') {
                        // Si une continuation de ligne est détectée
                        if (i + 1 >= data.length) {
                            // On ignore un `=` sans rien derrière (fin de chaîne)
                            continue;
                        } else if (data[i + 1] == '\r' && i + 2 < data.length && data[i + 2] == '\n') {
                            // Cas où `=` est suivi de `\r\n` : continuation de ligne
                            i += 2; // On saute le `\r\n`
                            continue;
                        }

                        // Vérification d'une séquence hexadécimale valide
                        if (i + 2 < data.length && isHexChar((char) data[i + 1]) && isHexChar((char) data[i + 2])) {
                            String hex = new String(data, i + 1, 2, StandardCharsets.ISO_8859_1);
                            buffer.write((char) Integer.parseInt(hex, 16));
                            i += 2; // Passer les deux caractères hexadécimaux
                        } else {
                            // Séquence invalide : on ajoute simplement le caractère `=`
                            buffer.write(data[i]);
                        }
                    } else {
                        // Pas un `=` encodé, ajouter directement au buffer
                        buffer.write(data[i]);
                    }
                }

                // Décoder le contenu en UTF-8
                String decodedContent = buffer.toString(StandardCharsets.UTF_8);

                // Normalize line breaks
                return decodedContent.replace("\r\n", "\n");
            } catch (Exception e) {
                throw new RuntimeException("Failed to decode quoted-printable", e);
            }

        }
        // Méthode utilitaire pour valider les caractères hexadécimaux
        private static boolean isHexChar(char c) {
            return (c >= '0' && c <= '9') || (c >= 'A' && c <= 'F') || (c >= 'a' && c <= 'f');
        }

    }



}
