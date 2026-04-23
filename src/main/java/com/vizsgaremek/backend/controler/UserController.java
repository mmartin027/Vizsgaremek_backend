package com.vizsgaremek.backend.controler;

import com.vizsgaremek.backend.DTO.LoginDto;
import com.vizsgaremek.backend.DTO.RegisterDto;
import com.vizsgaremek.backend.DTO.JwtResponse;
import com.vizsgaremek.backend.DTO.TokenRefreshRequest;
import com.vizsgaremek.backend.model.RefreshToken;
import com.vizsgaremek.backend.model.User;
import com.vizsgaremek.backend.model.UserPrincipal;
import com.vizsgaremek.backend.repository.UserRepository;
import com.vizsgaremek.backend.service.JwtService;
import com.vizsgaremek.backend.service.RefreshTokenService;
import com.vizsgaremek.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private UserService service;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDto registerDto) {
        System.out.println("--- TESZT: ANGULARBÓL ÉRKEZETT USERNAME: " + registerDto.getUsername() + " ---");
        if (registerDto.getPassword() == null || registerDto.getPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("A jelszó megadása kötelező!");
        }
        try {
            service.saveUser(registerDto);
            return ResponseEntity.ok("Sikeres regisztráció!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Hiba a regisztráció során: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        try {
            // Felhasználó megkeresése
            User user = service.findByUsername(loginDto.getUsername());

            if (!Boolean.TRUE.equals(user.getIsVerified())) {
                return ResponseEntity.status(401).body("Kérlek erősítsd meg az e-mail címedet a belépéshez!");
            }



            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getUsername(),
                            loginDto.getPassword()
                    )
            );

            if (authentication.isAuthenticated()) {
                UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

                String accessToken = jwtService.generateToken(userPrincipal, user.getId());
                RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());

                return ResponseEntity.ok(new JwtResponse(accessToken, refreshToken.getToken()));
            } else {
                return ResponseEntity.status(401).body("Sikertelen azonosítás");
            }

        } catch (Exception e) {

            return ResponseEntity.status(401).body("Hibás felhasználónév vagy jelszó!");
        }
    }

    @PostMapping("/verify-registration/{email}/{otp}")
    public ResponseEntity<?> verifyRegistration(@PathVariable String email, @PathVariable Integer otp) {
        try {
            User user = service.findByEmail(email);

            if (user.getIsVerified()) {
                return ResponseEntity.badRequest().body("Ez a fiók már hitelesítve van!");
            }

            if (!passwordEncoder.matches(String.valueOf(otp), user.getRegistrationOtpHash())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Érvénytelen kód!");
            }

            // Lejárat ellenőrzése
            if (user.getOtpExpiration().before(new Date())) {
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("A kód lejárt! Kérj újat.");
            }

            if (!passwordEncoder.matches(String.valueOf(otp), user.getRegistrationOtpHash())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Érvénytelen kód!");
            }
            user.setIsVerified(true);
            user.setRegistrationOtpHash(null);
            user.setOtpExpiration(null);


            userRepository.save(user);

            return ResponseEntity.ok("Fiók sikeresen aktiválva!");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Hiba a hitelesítés során: " + e.getMessage());
        }
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(@RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        try {
            RefreshToken token = refreshTokenService.findByToken(requestRefreshToken)
                    .orElseThrow(() -> new RuntimeException("A Refresh Token nem található!"));

            token = refreshTokenService.verifyExpiration(token);

            User user = token.getUser();
            UserPrincipal userPrincipal = new UserPrincipal(user);
            String newAccessToken = jwtService.generateToken(userPrincipal, user.getId());

            refreshTokenService.deleteByToken(token.getToken());
            RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user.getEmail());

            return ResponseEntity.ok(new JwtResponse(newAccessToken, newRefreshToken.getToken()));

        } catch (Exception e) {
            return ResponseEntity.status(401).body("Hiba a frissítés során: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserProfile(@PathVariable Integer id) {
        try {

            User user = service.getUserProfile(id);

            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Hiba: " + e.getMessage());
        }
    }
}