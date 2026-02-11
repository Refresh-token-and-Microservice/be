package com.example.demo_service.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HeaderAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String userEmail = request.getHeader("X-User-Email");
        String userId = request.getHeader("X-User-Id");
        String rolesString = request.getHeader("X-User-Roles");

        if (userEmail != null && rolesString != null) {
            // Parse roles from header (format: "[ADMIN, EMPLOYEE]" or "[ADMIN]")
            List<SimpleGrantedAuthority> authorities = parseRoles(rolesString);

            // Create authentication token
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userEmail,
                    null, authorities);

            // Set authentication in SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private List<SimpleGrantedAuthority> parseRoles(String rolesString) {
        // Remove brackets and split: "[ADMIN, EMPLOYEE]" -> ["ADMIN", "EMPLOYEE"]
        String cleaned = rolesString.replaceAll("[\\[\\]]", "").trim();

        if (cleaned.isEmpty()) {
            return List.of();
        }

        return Arrays.stream(cleaned.split(","))
                .map(String::trim)
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }
}
