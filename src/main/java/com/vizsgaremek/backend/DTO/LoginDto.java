package com.vizsgaremek.backend.DTO;

import lombok.Value;

import java.io.Serializable;


@Value
public class LoginDto implements Serializable {
    String username;
    String password;
}