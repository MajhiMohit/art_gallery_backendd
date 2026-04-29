package com.klu.artt_gallery.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class RootController {

    @GetMapping("/")
    public ResponseEntity<Map<String, String>> root() {
        return ResponseEntity.ok(Map.of(
            "app",     "ArtGallery Backend API",
            "status",  "running",
            "version", "1.0.0",
            "health",  "/api/auth/health"
        ));
    }
}
