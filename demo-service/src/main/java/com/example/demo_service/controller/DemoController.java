package com.example.demo_service.controller;

import com.example.demo_service.util.ResponseFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DemoController {

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> adminEndpoint() {
        return ResponseEntity.ok(ResponseFactory.payload("Hello Admin! You have access.", null));
    }

    @GetMapping("/employee")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public ResponseEntity<?> employeeEndpoint() {
        return ResponseEntity.ok(ResponseFactory.payload("Hello Employee! Work hard.", null));
    }

    @GetMapping("/demo")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> commonEndpoint() {
        return ResponseEntity.ok(ResponseFactory.payload("Hello! This is for everyone.", null));
    }
}
