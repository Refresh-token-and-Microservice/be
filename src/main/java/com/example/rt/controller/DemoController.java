package com.example.rt.controller;

import com.example.rt.util.ResponseFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
public class DemoController {

    @GetMapping
    public ResponseEntity<?> sayHello() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        return ResponseEntity.ok(ResponseFactory.payload(
                "Hello! You are authorized.",
                "Current User: " + authentication.getName() 
        ));
    }
}
