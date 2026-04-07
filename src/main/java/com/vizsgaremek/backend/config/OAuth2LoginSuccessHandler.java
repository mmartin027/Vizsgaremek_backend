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
import java.time.LocalDateTime;
import java.util.Set;
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

                newUser.setFirstName(oAuth2User.getAttribute("given_name"));
                newUser.setLastName(oAuth2User.getAttribute("family_name"));

                newUser.setGuid(UUID.randomUUID().toString());
                newUser.setAuthSecret(UUID.randomUUID().toString());
                newUser.setIsDeleted(false);

                roleRepository.findByName("ROLE_USER").ifPresent(role -> {
                    newUser.setRoles(Set.of(role));
                });

                return userRepository.save(newUser);
            });

            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);

            UserPrincipal userPrincipal = new UserPrincipal(user);
            String token = jwtService.generateToken(userPrincipal, user.getId());

            String frontendUrl = "https".equals(request.getHeader("X-Forwarded-Proto"))
                    ? "https://netparkolo.hu"
                    : "http://localhost:4200";

            String targetUrl = frontendUrl + "/login-success#token=" + token;
            getRedirectStrategy().sendRedirect(request, response, targetUrl);

        } catch (Exception e) {

            e.printStackTrace();

            String frontendUrl = "https".equals(request.getHeader("X-Forwarded-Proto"))
                    ? "https://netparkolo.hu"
                    : "http://localhost:4200";

            response.sendRedirect(frontendUrl + "/login?error=oauth2_error");
        }
    }
}