package com.klu.artt_gallery.mapper;

import com.klu.artt_gallery.dto.ArtworkDTO;
import com.klu.artt_gallery.entity.Artwork;

public class ArtworkMapper {

    public static Artwork toEntity(ArtworkDTO dto) {
        return Artwork.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .imageUrl(dto.getImageUrl())
                .price(dto.getPrice())
                .build();
    }

    public static ArtworkDTO toDTO(Artwork artwork) {
        ArtworkDTO dto = new ArtworkDTO();
        dto.setTitle(artwork.getTitle());
        dto.setDescription(artwork.getDescription());
        dto.setImageUrl(artwork.getImageUrl());
        dto.setPrice(artwork.getPrice());
        return dto;
    }
}