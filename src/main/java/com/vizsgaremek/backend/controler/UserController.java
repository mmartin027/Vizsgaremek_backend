package com.vizsgaremek.backend.controler; // Nálad controler a csomagnév

import com.vizsgaremek.backend.DTO.LoginDto;
import com.vizsgaremek.backend.DTO.RegisterDto;
import com.vizsgaremek.backend.DTO.JwtResponse; // ÚJ IMPORT
import com.vizsgaremek.backend.DTO.TokenRefreshRequest; // ÚJ IMPORT
import com.vizsgaremek.backend.model.RefreshToken; // ÚJ IMPORT
import com.vizsgaremek.backend.model.User;
import com.vizsgaremek.backend.model.UserPrincipal;
import com.vizsgaremek.backend.service.JwtService;
import com.vizsgaremek.backend.service.RefreshTokenService; // ÚJ IMPORT
import com.vizsgaremek.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

    // ÚJ: Injektáljuk a frissítő szervizt
    @Autowired
    private RefreshTokenService refreshTokenService;

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
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getUsername(),
                            loginDto.getPassword()
                    )
            );

            if (authentication.isAuthenticated()) {
                UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
                User user = service.findByUsername(loginDto.getUsername());

                String accessToken = jwtService.generateToken(userPrincipal, user.getId());
                RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());

                return ResponseEntity.ok(new JwtResponse(accessToken, refreshToken.getToken()));
            } else {
                return ResponseEntity.status(401).body("Sikertelen azonosítás");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(401).body("Hibás felhasználónév vagy jelszó!");
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

            // Rotation: régi törlése, új generálása
            refreshTokenService.deleteByToken(token.getToken());
            RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user.getEmail());

            return ResponseEntity.ok(new JwtResponse(newAccessToken, newRefreshToken.getToken()));

        } catch (Exception e) {
            return ResponseEntity.status(401).body("Hiba a frissítés során: " + e.getMessage());
        }
    }


}