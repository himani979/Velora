package com.velora.backend.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return "OPTIONS".equalsIgnoreCase(request.getMethod());
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        System.out.println("========== JWT FILTER ==========");
        System.out.println("Request: " + request.getMethod() + " " + request.getRequestURI());

        String authHeader = request.getHeader("Authorization");

        System.out.println("Authorization Header: " + authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            String token = authHeader.substring(7);

            try {

                System.out.println("Token received");

                boolean valid = jwtService.isTokenValid(token);

                System.out.println("JWT VALID = " + valid);

                if (valid) {

                    String email = jwtService.extractEmail(token);

                    System.out.println("Logged in email: " + email);


                    System.out.println("JWT VALID");


                    UserDetails userDetails =
                            customUserDetailsService.loadUserByUsername(email);

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authentication);

                    System.out.println("Authentication SET successfully");

                } else {

                    System.out.println("JWT INVALID");

                }

            } catch (Exception e) {

                System.out.println("JWT ERROR: " + e.getMessage());
                e.printStackTrace();
            }
        } else {

            System.out.println("NO BEARER TOKEN");

        }

        filterChain.doFilter(request, response);
    }
}