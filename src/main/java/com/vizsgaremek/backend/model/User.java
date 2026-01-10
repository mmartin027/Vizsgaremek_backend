package com.vizsgaremek.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "user") // Cseréld le a tényleges tábla nevedre!
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "password", nullable = true)
    private String password;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "phone")
    private String phone;


    @Column(name = "auth_secret")
    private String authSecret = "";  // <-- ALAPÉRTELMEZETT ÉRTÉK!

    @Column(name = "guid")
    private String guid;

    @Column(name = "provider")
    private String provider = "local";  // <-- ALAPÉRTELMEZETT ÉRTÉK!

    @Column(name = "reg_token")
    private String regToken;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;  // <-- ALAPÉRTELMEZETT ÉRTÉK!

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "register_finished_at")
    private LocalDateTime registerFinishedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (registerFinishedAt == null) {
            registerFinishedAt = LocalDateTime.now();
        }
        if (guid == null || guid.isEmpty()) {
            guid = java.util.UUID.randomUUID().toString();
        }
        if (authSecret == null) {
            authSecret = "";  // <-- FONTOS!
        }
        if (provider == null) {
            provider = "local";
        }
        if (isDeleted == null) {
            isDeleted = false;
        }
    }
}