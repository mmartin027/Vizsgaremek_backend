package com.vizsgaremek.backend.config;

import com.vizsgaremek.backend.service.JwtService;
import com.vizsgaremek.backend.service.UserDetailService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class    JwtFilter  extends OncePerRequestFilter {


    @Autowired
    JwtService jwtService;


    @Autowired
    ApplicationContext context;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {



        String authHeader  = request.getHeader("Authorization");
        String token = null;
        String userName = null;

        if(authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                userName = jwtService.extractUserName(token);
            } catch (ExpiredJwtException e) {

                System.out.println(" Lejárt JWT token: " + e.getMessage());
            } catch (Exception e) {

                System.out.println(" Érvénytelen JWT token: " + e.getMessage());
            }
        }

        if(userName != null && SecurityContextHolder.getContext().getAuthentication()==null){

            UserDetails userDetails = context.getBean(UserDetailService.class).loadUserByUsername(userName);




            if(jwtService.validateToken(token, userDetails)){
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);


            }

        }
        String path = request.getRequestURI();
        if (path.equals("/api/checkout/webhook") || path.startsWith("/api/auth/")) {
            filterChain.doFilter(request, response);
            return; // Ezzel kilépünk, a JWT ellenőrzés el sem indul!
        }
        filterChain.doFilter(request, response);
    }
}
