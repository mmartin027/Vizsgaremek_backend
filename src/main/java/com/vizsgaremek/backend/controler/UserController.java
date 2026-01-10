package com.vizsgaremek.backend.controler;


import com.vizsgaremek.backend.DTO.LoginDto;
import com.vizsgaremek.backend.DTO.RegisterDto;
import com.vizsgaremek.backend.model.User;
import com.vizsgaremek.backend.service.JwtService;
import com.vizsgaremek.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200") // Engedélyezi az Angular frontendet
public class UserController {

    @Autowired
    private UserService service;

    @Autowired
    AuthenticationManager authenticationManager;


    @Autowired
    private JwtService jwtService;


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDto registerDto) {

        if (registerDto.getPassword() == null || registerDto.getPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("A jelszó megadása kötelező!");
        }

        service.saveUser(registerDto);
        return ResponseEntity.ok("Sikeres regisztráció!");
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        try {
            // 1. Kisbetűs loginDto-t használunk!
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getUsername(),
                            loginDto.getPassword()
                    )
            );

            if (authentication.isAuthenticated()) {

                String token = jwtService.generateToken(loginDto.getUsername());


                return ResponseEntity.ok(token);
            } else {
                return ResponseEntity.status(401).body("Sikertelen azonosítás");
            }
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Hibás felhasználónév vagy jelszó!");
        }
    }

}