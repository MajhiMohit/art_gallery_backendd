package com.klu.artt_gallery.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.klu.artt_gallery.entity.Artwork;
import com.klu.artt_gallery.repository.ArtworkRepository;

@Service
public class ArtworkService {

    @Autowired
    private ArtworkRepository artworkRepository;

    @Autowired
    private ImageService imageService;

    // ── Fetch all OR filter by userId (artist dashboard) ─────────────────────
    public List<Artwork> getAllArtworks(Long artistId) {
        if (artistId != null) {
            return artworkRepository.findByUserId(artistId);
        }
        return artworkRepository.findAll();
    }

    // ── Fetch single artwork by id ────────────────────────────────────────────
    public Artwork getArtworkById(Long id) {
        return artworkRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Artwork not found with id: " + id));
    }

    // ── Create ────────────────────────────────────────────────────────────────
    public Artwork addArtwork(
            String title, String artist, double price, Long userId,
            String category, String era, String medium, String dimensions,
            String description, String culturalSignificance, String origin, String tags,
            boolean featured, double rating, int year,
            String imageUrl, MultipartFile image) {

        String resolvedImageUrl = resolveImageUrl(image, imageUrl);

        Artwork artwork = Artwork.builder()
                .title(title)
                .artist(artist)
                .price(price)
                .userId(userId)
                .category(category)
                .era(era)
                .medium(medium)
                .dimensions(dimensions)
                .description(description)
                .culturalSignificance(culturalSignificance)
                .origin(origin)
                .tags(tags)
                .featured(featured)
                .rating(rating)
                .year(year > 0 ? year : java.time.Year.now().getValue())
                .imageUrl(resolvedImageUrl)
                .sold(false)
                .views(0)
                .build();

        return artworkRepository.save(artwork);
    }

    // ── Update ────────────────────────────────────────────────────────────────
    public Artwork updateArtwork(
            Long id, String title, String artist, double price, Long userId,
            String category, String era, String medium, String dimensions,
            String description, String culturalSignificance, String origin, String tags,
            boolean featured, double rating, int year,
            String imageUrl, MultipartFile image) {

        Artwork existing = artworkRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Artwork not found with id: " + id));

        String resolvedImageUrl = resolveImageUrl(image, imageUrl);
        if (resolvedImageUrl == null || resolvedImageUrl.isBlank()) {
            resolvedImageUrl = existing.getImageUrl(); // keep old image if none provided
        }

        existing.setTitle(title);
        existing.setArtist(artist);
        existing.setPrice(price);
        if (userId != null) existing.setUserId(userId); // preserve userId if not sent
        existing.setCategory(category);
        existing.setEra(era);
        existing.setMedium(medium);
        existing.setDimensions(dimensions);
        existing.setDescription(description);
        existing.setCulturalSignificance(culturalSignificance);
        existing.setOrigin(origin);
        existing.setTags(tags);
        existing.setFeatured(featured);
        existing.setRating(rating);
        existing.setYear(year > 0 ? year : existing.getYear());
        existing.setImageUrl(resolvedImageUrl);

        return artworkRepository.save(existing);
    }

    // ── Delete ────────────────────────────────────────────────────────────────
    public void deleteArtwork(Long id) {
        artworkRepository.deleteById(id);
    }

    // ── Internal: upload to Cloudinary or fall back to provided URL ───────────
    private String resolveImageUrl(MultipartFile image, String imageUrl) {
        if (image != null && !image.isEmpty()) {
            try {
                return imageService.uploadImage(image);
            } catch (Exception e) {
                System.err.println("Cloudinary upload failed: " + e.getMessage());
            }
        }
        return (imageUrl != null && !imageUrl.isBlank()) ? imageUrl : null;
    }
}