package com.klu.artt_gallery.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.klu.artt_gallery.dto.*;
import com.klu.artt_gallery.entity.*;
import com.klu.artt_gallery.repository.UserRepository;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

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
                .build();

        User saved = userRepository.save(user);

        // Send welcome email
        try {
            String subject = "Welcome to ArtGallery!";
            String body = "Hi " + saved.getName() + ",\n\n"
                    + "Welcome to ArtGallery — your virtual art museum!\n\n"
                    + "Your account has been created successfully.\n"
                    + "Role: " + saved.getRole().name() + "\n"
                    + "Email: " + saved.getEmail() + "\n\n"
                    + "You can now log in at: http://localhost:5173/login\n\n"
                    + "Thank you for joining us!\n"
                    + "— The ArtGallery Team";
            emailService.sendEmail(saved.getEmail(), subject, body);
        } catch (Exception e) {
            // Email failure should not break registration
            System.err.println("Warning: Could not send welcome email - " + e.getMessage());
        }

        return new AuthResponse("User Registered Successfully", saved.getId(), saved.getName(), saved.getEmail(), saved.getRole().name());
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return new AuthResponse("Login Successful", user.getId(), user.getName(), user.getEmail(), user.getRole().name());
    }
}
