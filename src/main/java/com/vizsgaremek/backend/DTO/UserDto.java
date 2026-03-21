package com.vizsgaremek.backend.DTO;

import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.vizsgaremek.backend.model.User}
 */
@Value
public class UserDto implements Serializable {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String provider;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    private Boolean isDeleted;
}