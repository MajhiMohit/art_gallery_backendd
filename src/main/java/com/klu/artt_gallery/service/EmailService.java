package com.klu.artt_gallery.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Email service using Resend HTTP API.
 * Replaces JavaMailSender because Railway blocks outbound SMTP ports (587, 465).
 * Resend sends email over HTTPS — works everywhere.
 */
@Service
public class EmailService {

    @Value("${RESEND_API_KEY:re_GqNCHM74_4nSydBcDALSTrbgqEgGNohH8}")
    private String resendApiKey;

    // Free Resend plan: send from onboarding@resend.dev without domain verification
    @Value("${RESEND_FROM_EMAIL:ArtGallery <onboarding@resend.dev>}")
    private String fromEmail;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Async
    public void sendEmail(String to, String subject, String body) {
        try {
            // Build JSON payload for Resend API
            String jsonPayload = String.format(
                "{\"from\":\"%s\",\"to\":[\"%s\"],\"subject\":\"%s\",\"text\":\"%s\"}",
                fromEmail,
                to,
                subject,
                body.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "")
            );

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.resend.com/emails"))
                .header("Authorization", "Bearer " + resendApiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

            HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 || response.statusCode() == 201) {
                System.out.println("✅ Email sent via Resend to: " + to);
            } else {
                System.err.println("❌ Resend API error: " + response.statusCode() + " — " + response.body());
            }

        } catch (Exception e) {
            // Email failure is silently logged — does NOT block registration
            System.err.println("❌ Email sending failed (non-critical): " + e.getMessage());
        }
    }
}