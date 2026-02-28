package com.example.event_service.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class FeignClientInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String userId = request.getHeader("X-User-Id");
            String userRoles = request.getHeader("X-User-Roles");

            if (userId != null) {
                requestTemplate.header("X-User-Id", userId);
            }
            if (userRoles != null) {
                requestTemplate.header("X-User-Roles", userRoles);
            }

            // Also pass cookies if any exist
            String cookie = request.getHeader("Cookie");
            if (cookie != null) {
                requestTemplate.header("Cookie", cookie);
            }

            // Also pass authorization if it exists
            String auth = request.getHeader("Authorization");
            if (auth != null) {
                requestTemplate.header("Authorization", auth);
            }
        }
    }
}
