package com.vizsgaremek.backend.service;

import com.vizsgaremek.backend.DTO.MailBodyDTO;
import com.vizsgaremek.backend.DTO.RegisterDto;
import com.vizsgaremek.backend.model.Role; // ÚJ IMPORT!
import com.vizsgaremek.backend.model.User;
import com.vizsgaremek.backend.repository.RoleRepository; // ÚJ IMPORT!
import com.vizsgaremek.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private  UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;


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


        user.setIsVerified(false);
        int otp = new Random().nextInt(100_000, 999_999);
        user.setRegistrationOtpHash(passwordEncoder.encode(String.valueOf(otp)));
        user.setOtpExpiration(new Date(System.currentTimeMillis() + 15 * 60 * 1000));

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Hiba: ROLE_USER nem található az adatbázisban!"));
        user.setRoles(Set.of(userRole));

        User savedUser = userRepository.save(user);

        MailBodyDTO mailBody = new MailBodyDTO(
                savedUser.getEmail(),
                "Regisztráció megerősítése - ParkEasy",
                "Üdvözlünk a ParkEasy-ben! A fiókod aktiválásához szükséges kód: " + otp
        );
        emailService.sendSimpleMessage(mailBody);

        return savedUser;
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Felhasználó nem található: " + username));
    }

    public User findByEmail(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException());
    }

    public User getUserProfile(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Felhasználó nem található!"));

        user.setPassword(null);
        user.setAuthSecret(null);

        return user;
    }
}