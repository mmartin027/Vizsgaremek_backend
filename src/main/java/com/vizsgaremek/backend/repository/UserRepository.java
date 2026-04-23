package com.vizsgaremek.backend.repository;

import com.vizsgaremek.backend.model.User;
import org.springframework.data.jpa.repository.EntityGraph; // EZT IMPORTÁLD BE!
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    @Override
    @EntityGraph(attributePaths = {"roles"})
    List<User> findAll();

    Optional <User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.password = :password WHERE u.email = :email")
    void updatePassword(@Param("email") String email, @Param("password") String password);
}