package com.vizsgaremek.backend.DTO;

import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.vizsgaremek.backend.model.User}
 */
@Value
public class LoginDto implements Serializable {
    String username;
    String password;
}