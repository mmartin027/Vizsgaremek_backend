package com.vizsgaremek.backend.DTO;

import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.vizsgaremek.backend.model.User}
 */
@Value
public class UserDto implements Serializable {
    Long id;
    String username;
    String password;
    String email;
    String firstName;
    String lastName;
    String phone;
    String authSecret;
    String guid;
    String provider;
    String regToken;
    LocalDateTime createdAt;
    LocalDateTime deletedAt;
    Boolean isDeleted;
    LocalDateTime lastLogin;
    LocalDateTime registerFinishedAt;
}