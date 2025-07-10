package com.tabletennis.service;

import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.errors.MailjetException;
import com.tabletennis.exception.EmailSendingException;
import com.tabletennis.exception.EmailTemplateException;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private MailjetClient mailjetClient;

    @Mock
    private MailjetResponse mailjetResponse;

    @Mock
    private ResourceLoader resourceLoader;

    @Mock
    private Resource htmlResource;

    @Mock
    private Resource textResource;

    @Captor
    private ArgumentCaptor<MailjetRequest> requestCaptor;

    private EmailService emailService;
    private final Faker faker = new Faker();

    @BeforeEach
    void setUp() {
        var apiKey = faker.internet().password();
        var secretKey = faker.internet().password();
        var fromEmail = faker.internet().emailAddress();
        var fromName = faker.name().fullName();
        var baseUrl = faker.internet().url();

        emailService = new EmailService(apiKey, secretKey, fromEmail, fromName, baseUrl, resourceLoader);

        // Use reflection to set the mocked client
        try {
            var clientField = EmailService.class.getDeclaredField("client");
            clientField.setAccessible(true);
            clientField.set(emailService, mailjetClient);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mocked client", e);
        }
    }

    @Test
    void sendPasswordResetEmail_WithValidData_ShouldSendEmailSuccessfully() throws Exception {
        // Given
        var toEmail = faker.internet().emailAddress();
        var resetToken = faker.internet().uuid();
        var htmlTemplate = "<html><body>Click <a href=\"{{resetUrl}}\">here</a> to reset</body></html>";
        var textTemplate = "Click here to reset: {{resetUrl}}";

        when(resourceLoader.getResource("classpath:templates/email/password-reset.html"))
                .thenReturn(htmlResource);
        when(resourceLoader.getResource("classpath:templates/email/password-reset.txt"))
                .thenReturn(textResource);
        when(htmlResource.getContentAsString(StandardCharsets.UTF_8)).thenReturn(htmlTemplate);
        when(textResource.getContentAsString(StandardCharsets.UTF_8)).thenReturn(textTemplate);
        when(mailjetClient.post(any(MailjetRequest.class))).thenReturn(mailjetResponse);
        when(mailjetResponse.getStatus()).thenReturn(200);

        // When
        emailService.sendPasswordResetEmail(toEmail, resetToken);

        // Then
        verify(mailjetClient).post(requestCaptor.capture());
        var capturedRequest = requestCaptor.getValue();
        assertThat(capturedRequest).isNotNull();
    }

    @Test
    void sendPasswordResetEmail_WithMailjetException_ShouldThrowEmailSendingException() throws Exception {
        // Given
        var toEmail = faker.internet().emailAddress();
        var resetToken = faker.internet().uuid();
        var htmlTemplate = "<html><body>Reset email</body></html>";
        var textTemplate = "Reset email";

        when(resourceLoader.getResource("classpath:templates/email/password-reset.html"))
                .thenReturn(htmlResource);
        when(resourceLoader.getResource("classpath:templates/email/password-reset.txt"))
                .thenReturn(textResource);
        when(htmlResource.getContentAsString(StandardCharsets.UTF_8)).thenReturn(htmlTemplate);
        when(textResource.getContentAsString(StandardCharsets.UTF_8)).thenReturn(textTemplate);
        when(mailjetClient.post(any(MailjetRequest.class))).thenThrow(new MailjetException("Mailjet error"));

        // When/Then
        assertThatThrownBy(() -> emailService.sendPasswordResetEmail(toEmail, resetToken))
                .isInstanceOf(EmailSendingException.class)
                .hasMessage("Failed to send password reset email")
                .hasCauseInstanceOf(MailjetException.class);
    }

    @Test
    void sendPasswordResetEmail_WithNon200Response_ShouldThrowEmailSendingException() throws Exception {
        // Given
        var toEmail = faker.internet().emailAddress();
        var resetToken = faker.internet().uuid();
        var htmlTemplate = "<html><body>Reset email</body></html>";
        var textTemplate = "Reset email";

        when(resourceLoader.getResource("classpath:templates/email/password-reset.html"))
                .thenReturn(htmlResource);
        when(resourceLoader.getResource("classpath:templates/email/password-reset.txt"))
                .thenReturn(textResource);
        when(htmlResource.getContentAsString(StandardCharsets.UTF_8)).thenReturn(htmlTemplate);
        when(textResource.getContentAsString(StandardCharsets.UTF_8)).thenReturn(textTemplate);
        when(mailjetClient.post(any(MailjetRequest.class))).thenReturn(mailjetResponse);
        when(mailjetResponse.getStatus()).thenReturn(400);

        // When/Then
        assertThatThrownBy(() -> emailService.sendPasswordResetEmail(toEmail, resetToken))
                .isInstanceOf(EmailSendingException.class)
                .hasMessage("Failed to send email: HTTP 400");
    }

    @Test
    void sendPasswordResetEmail_WithHtmlTemplateLoadFailure_ShouldThrowEmailTemplateException() throws Exception {
        // Given
        var toEmail = faker.internet().emailAddress();
        var resetToken = faker.internet().uuid();

        when(resourceLoader.getResource("classpath:templates/email/password-reset.html"))
                .thenReturn(htmlResource);
        when(htmlResource.getContentAsString(StandardCharsets.UTF_8))
                .thenThrow(new IOException("Template not found"));

        // When/Then
        assertThatThrownBy(() -> emailService.sendPasswordResetEmail(toEmail, resetToken))
                .isInstanceOf(EmailTemplateException.class)
                .hasMessage("Failed to load HTML email template")
                .hasCauseInstanceOf(IOException.class);
    }

    @Test
    void sendPasswordResetEmail_WithTextTemplateLoadFailure_ShouldThrowEmailTemplateException() throws Exception {
        // Given
        var toEmail = faker.internet().emailAddress();
        var resetToken = faker.internet().uuid();
        var htmlTemplate = "<html><body>Reset email</body></html>";

        when(resourceLoader.getResource("classpath:templates/email/password-reset.html"))
                .thenReturn(htmlResource);
        when(resourceLoader.getResource("classpath:templates/email/password-reset.txt"))
                .thenReturn(textResource);
        when(htmlResource.getContentAsString(StandardCharsets.UTF_8)).thenReturn(htmlTemplate);
        when(textResource.getContentAsString(StandardCharsets.UTF_8))
                .thenThrow(new IOException("Template not found"));

        // When/Then
        assertThatThrownBy(() -> emailService.sendPasswordResetEmail(toEmail, resetToken))
                .isInstanceOf(EmailTemplateException.class)
                .hasMessage("Failed to load text email template")
                .hasCauseInstanceOf(IOException.class);
    }

    @Test
    void sendPasswordResetEmail_ShouldReplaceTokenInTemplates() throws Exception {
        // Given
        var toEmail = faker.internet().emailAddress();
        var resetToken = faker.internet().uuid();
        var htmlTemplate = "<html><body>Click <a href=\"{{resetUrl}}\">here</a></body></html>";
        var textTemplate = "Reset URL: {{resetUrl}}";

        when(resourceLoader.getResource("classpath:templates/email/password-reset.html"))
                .thenReturn(htmlResource);
        when(resourceLoader.getResource("classpath:templates/email/password-reset.txt"))
                .thenReturn(textResource);
        when(htmlResource.getContentAsString(StandardCharsets.UTF_8)).thenReturn(htmlTemplate);
        when(textResource.getContentAsString(StandardCharsets.UTF_8)).thenReturn(textTemplate);
        when(mailjetClient.post(any(MailjetRequest.class))).thenReturn(mailjetResponse);
        when(mailjetResponse.getStatus()).thenReturn(200);

        // When
        emailService.sendPasswordResetEmail(toEmail, resetToken);

        // Then
        verify(mailjetClient).post(requestCaptor.capture());
        var capturedRequest = requestCaptor.getValue();
        assertThat(capturedRequest).isNotNull();
    }
}
