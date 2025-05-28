package fr.hoenheimsports.contactservice.service;

import fr.hoenheimsports.contactservice.dto.EmailRequest;
import jakarta.mail.Address;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * Unit test class for the {@link EmailServiceImpl} email service.
 * This class verifies the ability to send an email and associated parameters (recipient, subject, content).
 */
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender; // Mock of the interface used for sending emails.

    @InjectMocks
    private EmailServiceImpl emailService; // Real service instance with mock injection.

    /**
     * Initialization of the service before each test by defining the "to" address.
     */
    @BeforeEach
    void setUp() {
        // Sets the destination "to" address for sending emails.
        ReflectionTestUtils.setField(emailService, "to", "test@example.com");
    }

    /**
     * Verifies that the {@link EmailServiceImpl#sendEmail(EmailRequest)}
     * method correctly sends an email with the specified parameters.
     *
     * @throws Exception if an error occurs during the creation or sending of the message.
     */
    @Test
    void shouldSendEmailSuccessfully() throws Exception {
        // Arrange: Prepare the required objects for the test.

        // Create a simulated MIME message.
        MimeMessage mimeMessage = new MimeMessage((Session) null);

        // Mock the creation of the MIME message using JavaMailSender.
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Define the input data.
        String from = "john.doe@example.com";
        String name = "John Doe";
        String body = "Hello, this is a test message.";
        var subject = "Notification de formulaire de contact de : " + name;
        var emailRequest = new EmailRequest(from, name, body);
        // Act: Call the method to test.
        emailService.sendEmail(emailRequest);

        // Assert: Verify the results.

        // Verify that the recipients were set correctly.
        Address[] recipients = mimeMessage.getRecipients(MimeMessage.RecipientType.TO);
        Assertions.assertThat(recipients)
                .isNotNull()
                .hasSize(1)
                .allMatch(address -> ((InternetAddress) address).getAddress().equals("test@example.com"));

        // Verify that the email subject matches the expected one.
        Assertions.assertThat(mimeMessage.getSubject()).isEqualTo(subject);

        // Verify that the email content includes the expected body.
        Assertions.assertThat(mimeMessage.getContent().toString()).contains(body);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void shouldThrowExceptionWhenEmailRequestIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> emailService.sendEmail(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("emailRequest"); // Si JVM inclut un message pertinent.
    }

    @Test
    void shouldContainWarningMessageInEmailBody() throws Exception {
        // Arrange : Préparation des mocks et des objets nécessaires
        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage); // Simule MimeMessage

        String from = "recipient@example.com";
        String name = "Test User";
        String body = "This is a test body message.";
        var emailRequest = new EmailRequest(from, name, body);

        String expectedWarningMessage = String.format(
                """
                        Attention : ce message a été écrit par %s (%s).
                        Merci de ne pas cliquer sur 'Répondre' pour répondre directement à cet email.
                        
                        """,
                name, from
        );

        // Act : Appel de la méthode
        emailService.sendEmail(emailRequest);

        // Assert: Verify the content of the message body.
        Assertions.assertThat(mimeMessage.getContent().toString())
                .contains(expectedWarningMessage)
                .contains(body); // Le corps du message doit aussi contenir le message customisé
    }


}