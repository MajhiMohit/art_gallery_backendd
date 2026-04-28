package com.klu.artt_gallery.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.klu.artt_gallery.entity.Artwork;
import com.klu.artt_gallery.service.ArtworkService;

@RestController
@RequestMapping("/api/artworks")
public class ArtworkController {

    @Autowired
    private ArtworkService artworkService;

    // GET /api/artworks              → all artworks
    // GET /api/artworks?artistId=5   → only artworks belonging to user #5
    @GetMapping
    public List<Artwork> getAllArtworks(
            @RequestParam(value = "artistId", required = false) Long artistId) {
        return artworkService.getAllArtworks(artistId);
    }

    // GET /api/artworks/{id}
    @GetMapping("/{id}")
    public Artwork getArtworkById(@PathVariable Long id) {
        return artworkService.getArtworkById(id);
    }

    // POST /api/artworks  — accepts multipart/form-data
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Artwork addArtwork(
            @RequestParam("title")                                               String title,
            @RequestParam("artist")                                              String artist,
            @RequestParam("price")                                               double price,
            @RequestParam(value = "userId",               required = false)      Long userId,
            @RequestParam(value = "category",             required = false)      String category,
            @RequestParam(value = "era",                  required = false)      String era,
            @RequestParam(value = "medium",               required = false)      String medium,
            @RequestParam(value = "dimensions",           required = false)      String dimensions,
            @RequestParam(value = "description",          required = false)      String description,
            @RequestParam(value = "culturalSignificance", required = false)      String culturalSignificance,
            @RequestParam(value = "origin",               required = false)      String origin,
            @RequestParam(value = "tags",                 required = false)      String tags,
            @RequestParam(value = "featured",             required = false, defaultValue = "false") boolean featured,
            @RequestParam(value = "rating",               required = false, defaultValue = "0")     double rating,
            @RequestParam(value = "year",                 required = false, defaultValue = "0")     int year,
            @RequestParam(value = "imageUrl",             required = false)      String imageUrl,
            @RequestParam(value = "image",                required = false)      MultipartFile image) {

        return artworkService.addArtwork(
                title, artist, price, userId,
                category, era, medium, dimensions,
                description, culturalSignificance, origin, tags,
                featured, rating, year, imageUrl, image);
    }

    // PUT /api/artworks/{id}
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Artwork updateArtwork(
            @PathVariable Long id,
            @RequestParam("title")                                               String title,
            @RequestParam("artist")                                              String artist,
            @RequestParam("price")                                               double price,
            @RequestParam(value = "userId",               required = false)      Long userId,
            @RequestParam(value = "category",             required = false)      String category,
            @RequestParam(value = "era",                  required = false)      String era,
            @RequestParam(value = "medium",               required = false)      String medium,
            @RequestParam(value = "dimensions",           required = false)      String dimensions,
            @RequestParam(value = "description",          required = false)      String description,
            @RequestParam(value = "culturalSignificance", required = false)      String culturalSignificance,
            @RequestParam(value = "origin",               required = false)      String origin,
            @RequestParam(value = "tags",                 required = false)      String tags,
            @RequestParam(value = "featured",             required = false, defaultValue = "false") boolean featured,
            @RequestParam(value = "rating",               required = false, defaultValue = "0")     double rating,
            @RequestParam(value = "year",                 required = false, defaultValue = "0")     int year,
            @RequestParam(value = "imageUrl",             required = false)      String imageUrl,
            @RequestParam(value = "image",                required = false)      MultipartFile image) {

        return artworkService.updateArtwork(
                id, title, artist, price, userId,
                category, era, medium, dimensions,
                description, culturalSignificance, origin, tags,
                featured, rating, year, imageUrl, image);
    }

    // DELETE /api/artworks/{id}
    @DeleteMapping("/{id}")
    public void deleteArtwork(@PathVariable Long id) {
        artworkService.deleteArtwork(id);
    }
}