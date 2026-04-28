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
 * Sends email via Brevo (formerly Sendinblue) HTTP API.
 * 
 * WHY BREVO?
 *   - Railway blocks ALL outbound SMTP (ports 25, 465, 587)
 *   - Brevo sends over HTTPS (port 443) — never blocked
 *   - Free plan: 300 emails/day, sends to ANY email (no domain required)
 *
 * SETUP: Set BREVO_API_KEY in Railway environment variables.
 */
@Service
public class EmailService {

    // Set BREVO_API_KEY as environment variable on Railway
    // Locally: email is skipped gracefully if key is not set
    @Value("${BREVO_API_KEY:NOT_CONFIGURED}")
    private String apiKey;

    // Sender email must match a verified sender in your Brevo account
    private static final String SENDER_EMAIL = "majjimohit77@gmail.com";
    private static final String SENDER_NAME  = "ArtGallery";

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Async
    public void sendEmail(String to, String subject, String body) {
        try {
            // Escape special chars for JSON
            String safeBody = body
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "");

            String json = String.format(
                "{" +
                  "\"sender\":{\"name\":\"%s\",\"email\":\"%s\"}," +
                  "\"to\":[{\"email\":\"%s\"}]," +
                  "\"subject\":\"%s\"," +
                  "\"textContent\":\"%s\"" +
                "}",
                SENDER_NAME, SENDER_EMAIL, to, subject, safeBody
            );

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.brevo.com/v3/smtp/email"))
                    .header("api-key", apiKey)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());

            if (res.statusCode() == 201 || res.statusCode() == 200) {
                System.out.println("✅ Email sent via Brevo to: " + to);
            } else {
                System.err.println("❌ Brevo error " + res.statusCode() + ": " + res.body());
            }

        } catch (Exception e) {
            System.err.println("❌ Email failed (non-critical): " + e.getMessage());
        }
    }
}