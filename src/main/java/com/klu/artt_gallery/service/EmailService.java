package com.klu.artt_gallery.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Sends transactional email via Brevo (https://api.brevo.com).
 * Uses HTTPS (port 443) — works on Railway/Render with no SMTP port issues.
 */
@Service
public class EmailService {

    @Value("${brevo.api.key}")
    private String apiKey;

    @Value("${brevo.sender.email}")
    private String senderEmail;

    @Value("${brevo.sender.name}")
    private String senderName;

    private static final String BREVO_API_URL = "https://api.brevo.com/v3/smtp/email";

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Async
    public void sendEmail(String to, String subject, String body) {
        try {
            // Escape special characters for JSON
            String safeBody = body
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "");

            String safeSubject = subject
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"");

            String json = String.format(
                "{" +
                "\"sender\":{\"name\":\"%s\",\"email\":\"%s\"}," +
                "\"to\":[{\"email\":\"%s\"}]," +
                "\"subject\":\"%s\"," +
                "\"textContent\":\"%s\"" +
                "}",
                senderName, senderEmail, to, safeSubject, safeBody
            );

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(BREVO_API_URL))
                    .header("api-key", apiKey)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());

            if (res.statusCode() == 200 || res.statusCode() == 201) {
                System.out.println("✅ Email sent via Brevo to: " + to);
            } else {
                System.err.println("❌ Brevo error " + res.statusCode() + ": " + res.body());
            }
        } catch (Exception e) {
            System.err.println("❌ Email failed: " + e.getMessage());
        }
    }
}