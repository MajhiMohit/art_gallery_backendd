package com.klu.artt_gallery.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.klu.artt_gallery.entity.Artwork;

public interface ArtworkRepository extends JpaRepository<Artwork, Long> {
    List<Artwork> findByUserId(Long userId);
}