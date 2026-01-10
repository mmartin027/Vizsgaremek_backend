package com.vizsgaremek.backend.config;

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
import java.time.LocalDateTime;
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

        try {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            String email = oAuth2User.getAttribute("email");
            String name = oAuth2User.getAttribute("name");
            String picture = oAuth2User.getAttribute("picture");

            System.out.println("Sikeres Google login: " + email);

            User user = userRepository.findByEmail(email)
                    .orElseGet(() -> {
                        User newUser = new User();
                        newUser.setEmail(email);
                        newUser.setUsername(email);

                        if (name != null && name.contains(" ")) {
                            String[] nameParts = name.split(" ", 2);
                            newUser.setFirstName(nameParts[0]);
                            newUser.setLastName(nameParts[1]);
                        } else {
                            newUser.setFirstName(name != null ? name : "");
                            newUser.setLastName("");
                        }

                        newUser.setProvider("GOOGLE");
                        newUser.setPassword(null);
                        newUser.setAuthSecret(UUID.randomUUID().toString());
                        newUser.setPhone("");
                        newUser.setGuid(UUID.randomUUID().toString());

                        // JAVÍTVA: LocalDateTime.now() használata
                        newUser.setCreatedAt(LocalDateTime.now());
                        newUser.setIsDeleted(false);
                        newUser.setRegToken(UUID.randomUUID().toString());
                        newUser.setRegisterFinishedAt(LocalDateTime.now());

                        return userRepository.save(newUser);
                    });

            // JAVÍTVA: Itt is LocalDateTime.now()
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);

            String token = jwtService.generateToken(user.getEmail());

            String targetUrl = "http://localhost:4200/login-success?token=" + token;

            System.out.println("Átirányítás ide: " + targetUrl);
            getRedirectStrategy().sendRedirect(request, response, targetUrl);

        } catch (Exception e) {
            System.err.println("Hiba a Success Handlerben: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("http://localhost:4200/login?error=oauth2_error");
        }
    }}