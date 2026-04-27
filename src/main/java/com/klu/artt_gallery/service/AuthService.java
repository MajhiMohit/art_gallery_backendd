package com.klu.artt_gallery.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.klu.artt_gallery.dto.*;
import com.klu.artt_gallery.entity.*;
import com.klu.artt_gallery.repository.UserRepository;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Value("${FRONTEND_URL:http://localhost:5173}")
    private String frontendUrl;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already in use");
        }

        Role role = Role.VISITOR;
        if (request.getRole() != null) {
            try {
                role = Role.valueOf(request.getRole().toUpperCase());
            } catch (IllegalArgumentException e) {
                role = Role.VISITOR;
            }
        }

        String token = UUID.randomUUID().toString();

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .emailVerified(false)
                .verificationToken(token)
                .build();

        User saved = userRepository.save(user);

        // Send verification email (async, non-blocking)
        String verifyLink = frontendUrl + "/verify-email?token=" + token;
        String subject = "Verify your ArtGallery email address";
        String body = "Hi " + saved.getName() + ",\n\n"
                + "Thank you for registering at ArtGallery!\n\n"
                + "Please click the link below to verify your email address:\n"
                + verifyLink + "\n\n"
                + "This link will activate your account.\n\n"
                + "If you did not register, please ignore this email.\n\n"
                + "— The ArtGallery Team";
        emailService.sendEmail(saved.getEmail(), subject, body);

        return new AuthResponse(
                "Registration successful! Please check your email to verify your account.",
                saved.getId(), saved.getName(), saved.getEmail(), saved.getRole().name()
        );
    }

    public String verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired verification link."));

        if (user.isEmailVerified()) {
            return "Email already verified. You can log in.";
        }

        user.setEmailVerified(true);
        user.setVerificationToken(null);
        userRepository.save(user);
        return "Email verified successfully! You can now log in.";
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // Block unverified users from logging in
        if (!user.isEmailVerified()) {
            throw new RuntimeException("EMAIL_NOT_VERIFIED");
        }

        return new AuthResponse("Login Successful",
                user.getId(), user.getName(), user.getEmail(), user.getRole().name());
    }
}
