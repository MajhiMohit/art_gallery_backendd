package com.klu.artt_gallery.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String message;
    private Long id;
    private String name;
    private String email;
    private String role;
    private String token;  // JWT token returned on login

    // Backward-compatible constructor (no token)
    public AuthResponse(String message, Long id, String name, String email, String role) {
        this.message = message;
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.token = null;
    }
}
