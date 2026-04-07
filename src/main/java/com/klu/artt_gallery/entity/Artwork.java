package com.klu.artt_gallery.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "artwork")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Artwork {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String artist;
    private String description;
    private String imageUrl;
    private double price;

    private String category;
    private String era;
    private String medium;
    private String dimensions;
    private String origin;
    private String tags;

    @Column(name = "cultural_significance", columnDefinition = "TEXT")
    private String culturalSignificance;

    private boolean featured;
    private double rating;
    private int year;
    private boolean sold;
    private int views;

    // Links artwork to the artist's user account (userId from the users table)
    private Long userId;
}