package com.vizsgaremek.backend.service;

import com.vizsgaremek.backend.DTO.RegisterDto;
import com.vizsgaremek.backend.model.Role; // ÚJ IMPORT!
import com.vizsgaremek.backend.model.User;
import com.vizsgaremek.backend.repository.RoleRepository; // ÚJ IMPORT!
import com.vizsgaremek.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // 1. BE KELL INJEKTÁLNI A ROLE REPOSITORY-T IS!
    @Autowired
    private RoleRepository roleRepository;

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
        user.setProvider("LOCAL");

        user.setGuid(UUID.randomUUID().toString());
        user.setAuthSecret(UUID.randomUUID().toString());

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Hiba: ROLE_USER nem található az adatbázisban!"));

        // Hozzáadjuk a felhasználóhoz az alapjogot
        user.setRoles(Set.of(userRole));

        return userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Felhasználó nem található: " + username));
    }
}