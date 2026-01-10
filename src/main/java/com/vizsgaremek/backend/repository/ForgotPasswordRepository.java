package com.vizsgaremek.backend.repository;

import com.vizsgaremek.backend.model.ForgotPassword;
import com.vizsgaremek.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ForgotPasswordRepository extends JpaRepository<ForgotPassword, Long> {
    Optional<ForgotPassword> findByOtpAndUser(Integer otp, User user);
}