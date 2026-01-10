package com.vizsgaremek.backend.service;

import com.vizsgaremek.backend.DTO.RegisterDto;
import com.vizsgaremek.backend.model.User;
import com.vizsgaremek.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User saveUser(RegisterDto dto) {
        User user = new User();

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhone(dto.getPhone());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());

        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        user.setCreatedAt(LocalDateTime.now());
        user.setIsDeleted(false);
        user.setProvider("LOCAL"); // Jelezzük, hogy ez nem Google login


        user.setGuid(UUID.randomUUID().toString());
        user.setAuthSecret(UUID.randomUUID().toString());

        return userRepository.save(user);
    }
}