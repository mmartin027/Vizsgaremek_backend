package com.vizsgaremek.backend.repository;

import com.vizsgaremek.backend.model.RefreshToken;
import com.vizsgaremek.backend.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);


    @Transactional
    int deleteByToken(String token);

    int deleteByUser(User user);
}