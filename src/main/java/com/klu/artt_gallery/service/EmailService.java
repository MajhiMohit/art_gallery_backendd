package com.klu.artt_gallery.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Email service using JavaMailSender (Gmail SMTP + App Password).
 * Works on Railway — port 587 STARTTLS is allowed.
 * Requires GMAIL_USER and GMAIL_APP_PASSWORD env vars on Railway.
 */
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async
    public void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, false); // plain text

            mailSender.send(message);
            System.out.println("✅ Email sent to: " + to);

        } catch (Exception e) {
            System.err.println("❌ Email failed to: " + to + " | Error: " + e.getMessage());
            // Non-blocking — registration still succeeds even if email fails
        }
    }
}