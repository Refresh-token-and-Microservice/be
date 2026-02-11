package com.example.api_gateway.config;

import com.example.api_gateway.service.JwtService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

@Component
@RequiredArgsConstructor
public class AuthenticationFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    private final JwtService jwtService;

    @Override
    public ServerResponse filter(ServerRequest request, HandlerFunction<ServerResponse> next) throws Exception {
        String token = extractToken(request);

        if (token == null || !jwtService.isTokenValid(token)) {
            return ServerResponse.status(HttpStatus.UNAUTHORIZED).build();
        }

        Claims claims = jwtService.extractAllClaims(token);
        String username = claims.getSubject();
        Object userId = claims.get("id");
        Object roles = claims.get("roles");

        // Inject information into headers for downstream services
        ServerRequest modifiedRequest = ServerRequest.from(request)
                .header("X-User-Email", username)
                .header("X-User-Id", userId != null ? userId.toString() : "")
                .header("X-User-Roles", roles != null ? roles.toString() : "")
                .build();

        return next.handle(modifiedRequest);
    }

    private String extractToken(ServerRequest request) {
        // Try to get from Cookie first (as per existing logic)
        return request.cookies().getFirst("access_token") != null
                ? request.cookies().getFirst("access_token").getValue()
                : null;
    }
}
