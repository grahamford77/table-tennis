package com.tabletennis.service;

import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.resource.Emailv31;
import com.tabletennis.exception.EmailSendingException;
import com.tabletennis.exception.EmailTemplateException;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Service for sending emails using Mailjet API
 */
@Service
@Slf4j
public class EmailService {

    private final MailjetClient client;
    private final String fromEmail;
    private final String fromName;
    private final String baseUrl;
    private final ResourceLoader resourceLoader;

    public EmailService(@Value("${mailjet.api.key}") String apiKey,
                       @Value("${mailjet.secret.key}") String secretKey,
                       @Value("${app.email.from}") String fromEmail,
                       @Value("${app.email.from-name}") String fromName,
                       @Value("${app.base-url}") String baseUrl,
                       ResourceLoader resourceLoader) {
        ClientOptions options = ClientOptions.builder()
                .apiKey(apiKey)
                .apiSecretKey(secretKey)
                .build();
        this.client = new MailjetClient(options);
        this.fromEmail = fromEmail;
        this.fromName = fromName;
        this.baseUrl = baseUrl;
        this.resourceLoader = resourceLoader;
    }

    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        try {
            var resetUrl = baseUrl + "/reset-password?token=" + resetToken;

            var request = new MailjetRequest(Emailv31.resource)
                .property(Emailv31.MESSAGES, new JSONArray()
                    .put(new JSONObject()
                        .put(Emailv31.Message.FROM, new JSONObject()
                            .put("Email", fromEmail)
                            .put("Name", fromName))
                        .put(Emailv31.Message.TO, new JSONArray()
                            .put(new JSONObject()
                                .put("Email", toEmail)))
                        .put(Emailv31.Message.SUBJECT, "Reset Your Password - Rightmove Table Tennis Portal")
                        .put(Emailv31.Message.HTMLPART, buildPasswordResetEmailHtml(resetUrl))
                        .put(Emailv31.Message.TEXTPART, buildPasswordResetEmailText(resetUrl))));

            MailjetResponse response = client.post(request);

            if (response.getStatus() == 200) {
                log.info("Password reset email sent successfully to: {}", toEmail);
            } else {
                log.error("Failed to send password reset email to: {}. Status: {}, Response: {}",
                         toEmail, response.getStatus(), response.getData());
                throw new EmailSendingException("Failed to send email: HTTP " + response.getStatus());
            }

        } catch (MailjetException e) {
            log.error("Error sending password reset email to: {}", toEmail, e);
            throw new EmailSendingException("Failed to send password reset email", e);
        }
    }

    private String buildPasswordResetEmailHtml(String resetUrl) {
        try {
            var resource = resourceLoader.getResource("classpath:templates/email/password-reset.html");
            var content = resource.getContentAsString(StandardCharsets.UTF_8);
            return content.replace("{{resetUrl}}", resetUrl);
        } catch (IOException e) {
            log.error("Failed to load password reset HTML template", e);
            throw new EmailTemplateException("Failed to load HTML email template", e);
        }
    }

    private String buildPasswordResetEmailText(String resetUrl) {
        try {
            var resource = resourceLoader.getResource("classpath:templates/email/password-reset.txt");
            var content = resource.getContentAsString(StandardCharsets.UTF_8);
            return content.replace("{{resetUrl}}", resetUrl);
        } catch (IOException e) {
            log.error("Failed to load password reset text template", e);
            throw new EmailTemplateException("Failed to load text email template", e);
        }
    }
}
