package com.vizsgaremek.backend.controler;

import com.vizsgaremek.backend.DTO.LoginDto;
import com.vizsgaremek.backend.DTO.RegisterDto;
import com.vizsgaremek.backend.model.User;
import com.vizsgaremek.backend.model.UserPrincipal; // Fontos import!
import com.vizsgaremek.backend.service.JwtService;
import com.vizsgaremek.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    @Autowired
    private UserService service;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDto registerDto) {
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
            // 1. Hitelesítés elvégzése
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getUsername(),
                            loginDto.getPassword()
                    )
            );

            if (authentication.isAuthenticated()) {
                // 2. A hitelesített UserPrincipal kinyerése (ebben benne vannak a Role-ok)
                UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

                // 3. A felhasználó entitás lekérése az ID miatt
                User user = service.findByUsername(loginDto.getUsername());

                String token = jwtService.generateToken(userPrincipal, user.getId());

                Map<String, String> response = new HashMap<>();
                response.put("token", token);

                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(401).body("Sikertelen azonosítás");
            }
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Hibás felhasználónév vagy jelszó!");
        }
    }
}