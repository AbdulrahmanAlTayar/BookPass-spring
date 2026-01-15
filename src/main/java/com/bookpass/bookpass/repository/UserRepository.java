package com.bookpass.bookpass.repository;

import com.bookpass.bookpass.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User> findByResetToken(String resetToken);

    // Dashboard Stats
    long countByRole(String role);
    List<User> findTop5ByOrderByCreatedAtDesc();
    long countByUpdatedAtAfter(LocalDateTime date);
}