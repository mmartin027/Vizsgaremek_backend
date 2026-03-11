package com.vizsgaremek.backend.config;

import com.vizsgaremek.backend.model.User;
import com.vizsgaremek.backend.model.UserPrincipal;
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
import com.vizsgaremek.backend.repository.RoleRepository;

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

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        try {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            String email = oAuth2User.getAttribute("email");

            User user = userRepository.findByEmail(email).orElseGet(() -> {
                User newUser = new User();
                newUser.setEmail(email);
                newUser.setUsername(email);
                newUser.setProvider("GOOGLE");
                // ... (név beállítások maradhatnak) ...

                roleRepository.findByName("ROLE_USER").ifPresent(role -> {
                    // Hozzáadjuk a felhasználóhoz (a User entitásodban lévő Set<Role> roles mezőbe)
                    newUser.getRoles().add(role);
                });

                return userRepository.save(newUser);
            });

            // 2. Utolsó belépés frissítése
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);


            UserPrincipal userPrincipal = new UserPrincipal(user);

            String token = jwtService.generateToken(userPrincipal,(user.getId()));

            String targetUrl = "http://localhost:4200/login-success#token=" + token;
            getRedirectStrategy().sendRedirect(request, response, targetUrl);

        } catch (Exception e) {
            response.sendRedirect("http://localhost:4200/login?error=oauth2_error");
        }
    }
}