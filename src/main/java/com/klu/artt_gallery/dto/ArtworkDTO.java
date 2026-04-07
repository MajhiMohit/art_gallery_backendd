package com.klu.artt_gallery.dto;

import lombok.Data;

@Data
public class ArtworkDTO {
    private String title;
    private String description;
    private String imageUrl;
    private double price;
}