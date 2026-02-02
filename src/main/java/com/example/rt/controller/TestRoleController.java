package com.example.rt.controller;

import com.example.rt.util.ResponseFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestRoleController {

    @GetMapping("/admin")
    public ResponseEntity<?> adminEndpoint() {
        return ResponseEntity.ok(ResponseFactory.payload("Hello Admin! You have access.", null));
    }

    @GetMapping("/employee")
    public ResponseEntity<?> employeeEndpoint() {
        return ResponseEntity.ok(ResponseFactory.payload("Hello Employee! Work hard.", null));
    }

    @GetMapping("/demo")
    public ResponseEntity<?> commonEndpoint() {
        return ResponseEntity.ok(ResponseFactory.payload("Hello! This is for everyone.", null));
    }
}