package com.vizsgaremek.backend.security;

import com.vizsgaremek.backend.model.User;
import com.vizsgaremek.backend.repository.UserRepository;
import com.vizsgaremek.backend.service.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@Component


public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String picture = oAuth2User.getAttribute("picture");

        System.out.println(" Sikeres Google login: " + email);

        // Email alapján keresünk/hozunk létre user-t
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();

                    // Google adatok
                    newUser.setEmail(email);
                    newUser.setUsername(email); // Username = email

                    // Név szétbontása (ha van szóköz)
                    if (name != null && name.contains(" ")) {
                        String[] nameParts = name.split(" ", 2);
                        newUser.setFirstName(nameParts[0]);
                        newUser.setLastName(nameParts[1]);
                    } else {
                        newUser.setFirstName(name != null ? name : "");
                        newUser.setLastName("");
                    }

                    newUser.setProvider("GOOGLE");
                    newUser.setProfilePicture(picture);

                    // Kötelező mezők kitöltése
                    newUser.setPassword(null); // Google user-nek nincs jelszava
                    newUser.setAuthSecret(UUID.randomUUID().toString());
                    newUser.setPhone(""); // Később kitöltheti
                    newUser.setGuid(UUID.randomUUID().toString());
                    newUser.setCreatedAt(Instant.now());
                    newUser.setIsDeleted(false);
                    newUser.setRegToken(UUID.randomUUID().toString());
                    newUser.setRegisterFinishedAt(Instant.now()); // Google user azonnal kész
                    newUser.setImg("default_path");

                    return userRepository.save(newUser);
                });


        // Utolsó bejelentkezés frissítése
        user.setLastLogin(Instant.now());
        userRepository.save(user);

        // JWT token generálás
        String token = jwtService.generateToken(user.getEmail());

        // Átirányítás a frontra
        String targetUrl = "http://localhost:4200/login-success?token=" + token;
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}