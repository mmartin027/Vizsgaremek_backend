package com.vizsgaremek.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @ColumnDefault("'default_path'")
    @Column(name = "img", nullable = false)
    private String img;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "provider", length = 20)
    private String provider; // "LOCAL" vagy "GOOGLE"

    @Column(name = "profile_picture", length = 500)
    private String profilePicture; // Google profilkép URL (opcionális)

    @Column(name = "username", nullable = false,unique = true, length = 50)
    private String username;

    @Lob
    @Column(name = "password")
    private String password;

    @Column(name = "auth_secret", nullable = false, length = 64)
    private String authSecret;

    @Column(name = "phone", nullable = false, length = 30)
    private String phone;

    @Column(name = "guid", length = 64)
    private String guid;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @ColumnDefault("0")
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "last_login")
    private Instant lastLogin;

    @Column(name = "register_finished_at")
    private Instant registerFinishedAt;

    @Column(name = "reg_token", nullable = false, length = 64)
    private String regToken;

}