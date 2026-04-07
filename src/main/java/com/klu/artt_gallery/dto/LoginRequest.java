package com.klu.artt_gallery.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}