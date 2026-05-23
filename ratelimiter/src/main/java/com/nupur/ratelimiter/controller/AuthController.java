package com.nupur.ratelimiter.controller;

import com.nupur.ratelimiter.service.RateLimiterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private RateLimiterService rateLimiterService;

    // POST /auth/register
    // Body: { "username": "nupur" }
    // Returns: { "token": "xxxx-xxxx-xxxx" }
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        try {
            String username = body.get("username");
            String token = rateLimiterService.registerUser(username);
            return ResponseEntity.ok(Map.of(
                "message", "User registered successfully",
                "username", username,
                "token", token
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}