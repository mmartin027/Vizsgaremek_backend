package com.vizsgaremek.backend.repository;

import com.vizsgaremek.backend.model.ForgotPassword;
import com.vizsgaremek.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface ForgotPasswordRepository extends JpaRepository<ForgotPassword, Long> {


    Optional<ForgotPassword> findByUser(User user);

    @Transactional
    @Modifying
    void deleteByUser(User user);
}