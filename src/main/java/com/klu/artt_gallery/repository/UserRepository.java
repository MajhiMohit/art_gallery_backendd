package com.klu.artt_gallery.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.klu.artt_gallery.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByVerificationToken(String token);
}