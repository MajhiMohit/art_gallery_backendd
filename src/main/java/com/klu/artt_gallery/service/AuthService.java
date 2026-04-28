package com.klu.artt_gallery.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.klu.artt_gallery.dto.*;
import com.klu.artt_gallery.entity.*;
import com.klu.artt_gallery.repository.UserRepository;
import com.klu.artt_gallery.util.JwtUtil;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${FRONTEND_URL:https://art-gallery-new-seven.vercel.app}")
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

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .emailVerified(true)           // Auto-verified — no email verification step
                .verificationToken(UUID.randomUUID().toString())
                .build();

        User saved = userRepository.save(user);

        // Send welcome email (async, non-blocking)
        String subject = "Welcome to ArtGallery! 🎨";
        String body = "Hi " + saved.getName() + ",\n\n"
                + "You have successfully registered at ArtGallery!\n\n"
                + "Your account details:\n"
                + "  Name : " + saved.getName() + "\n"
                + "  Email: " + saved.getEmail() + "\n"
                + "  Role : " + saved.getRole().name() + "\n\n"
                + "You can log in at: " + frontendUrl + "/login\n\n"
                + "Enjoy exploring the gallery!\n\n"
                + "— The ArtGallery Team";

        emailService.sendEmail(saved.getEmail(), subject, body);

        return new AuthResponse(
                "Registration successful! A welcome email has been sent to " + saved.getEmail(),
                saved.getId(), saved.getName(), saved.getEmail(), saved.getRole().name()
        );
    }

    public String verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired verification link."));
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

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().name());

        return new AuthResponse("Login Successful",
                user.getId(), user.getName(), user.getEmail(), user.getRole().name(), token);
    }
}
