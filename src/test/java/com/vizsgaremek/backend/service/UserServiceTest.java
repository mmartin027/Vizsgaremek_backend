package com.vizsgaremek.backend.service;

import com.vizsgaremek.backend.DTO.MailBodyDTO;
import com.vizsgaremek.backend.DTO.RegisterDto;
import com.vizsgaremek.backend.model.Role;
import com.vizsgaremek.backend.model.User;
import com.vizsgaremek.backend.repository.RoleRepository;
import com.vizsgaremek.backend.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tesztek")
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @Test
    @DisplayName("Sikeres regisztráció (Email küldéssel)")
    void testSaveUser_Success() {
        // Arrange
        RegisterDto dto = new RegisterDto();
        dto.setFirstName("Teszt");
        dto.setLastName("Elek");
        dto.setUsername("tesztelek");
        dto.setEmail("teszt@elek.hu");
        dto.setPassword("Titkos123");

        Role userRole = new Role();
        userRole.setName("ROLE_USER");

        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(anyString())).thenReturn("hashed_password");

        when(userRepository.save(any(User.class))).thenAnswer(i -> {
            User u = i.getArgument(0);
            u.setId(1L);
            return u;
        });

        User result = userService.saveUser(dto);

        // Assert
        assertNotNull(result);
        assertEquals("tesztelek", result.getUsername());
        assertFalse(result.getIsVerified()); // Alapból false-nak kell lennie
        assertEquals("hashed_password", result.getPassword());

        verify(emailService, times(1)).sendSimpleMessage(any(MailBodyDTO.class));
    }

    @Test
    @DisplayName("Regisztráció hiba: Nincs ilyen Role az adatbázisban")
    void testSaveUser_RoleNotFound_ThrowsException() {
        RegisterDto dto = new RegisterDto();
        dto.setPassword("Titkos123");

        when(passwordEncoder.encode(anyString())).thenReturn("hashed_password");
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.saveUser(dto));

        assertTrue(ex.getMessage().contains("ROLE_USER nem található"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Felhasználó keresése - Sikeres")
    void testFindByUsername_Success() {
        // Arrange
        User user = new User();
        user.setUsername("admin");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));

        User result = userService.findByUsername("admin");

        assertEquals("admin", result.getUsername());
    }

    @Test
    @DisplayName("Felhasználó keresése - Nem található (Kivétel dobása)")
    void testFindByUsername_NotFound() {
        when(userRepository.findByUsername("ismeretlen")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.findByUsername("ismeretlen"));
    }

    @Test
    @DisplayName("Profil lekérése (Érzékeny adatok nullázása biztonsági okokból)")
    void testGetUserProfile_ClearsSensitiveData() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setPassword("supersecret_hash");
        user.setAuthSecret("secret_token_123");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        User result = userService.getUserProfile(1);

        assertNotNull(result);
        assertNull(result.getPassword(), "A jelszónak null-nak kell lennie a profil lekérésekor!");
        assertNull(result.getAuthSecret(), "Az authSecret-nek null-nak kell lennie a profil lekérésekor!");
    }
}