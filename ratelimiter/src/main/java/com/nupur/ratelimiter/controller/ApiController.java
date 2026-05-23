package com.nupur.ratelimiter.controller;

import com.nupur.ratelimiter.service.RateLimiterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private RateLimiterService rateLimiterService;

    // This is the protected endpoint — requires API token in header
    // GET /api/data
    // Header: X-API-TOKEN: your-token-here
    @GetMapping("/data")
    public ResponseEntity<?> getData(@RequestHeader("X-API-TOKEN") String token) {
        boolean allowed = rateLimiterService.isAllowed(token, "/api/data");

        if (!allowed) {
            return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)  // 429
                .body(Map.of(
                    "error", "Rate limit exceeded",
                    "message", "You have exceeded your request limit. Please wait and try again.",
                    "status", 429
                ));
        }

        return ResponseEntity.ok(Map.of(
            "message", "Success! Here is your data.",
            "data", "Sample response data",
            "status", 200
        ));
    }

    // GET /api/stats
    // Header: X-API-TOKEN: your-token-here
    // Returns request stats for this token
    @GetMapping("/stats")
    public ResponseEntity<?> getStats(@RequestHeader("X-API-TOKEN") String token) {
        var user = rateLimiterService.getUserByToken(token);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Invalid token"));
        }

        var logs = rateLimiterService.getLogsForToken(token);
        long allowed = logs.stream().filter(l -> l.getStatus().equals("ALLOWED")).count();
        long blocked = logs.stream().filter(l -> l.getStatus().equals("BLOCKED")).count();

        return ResponseEntity.ok(Map.of(
            "username", user.get().getUsername(),
            "requestLimit", user.get().getRequestLimit(),
            "totalRequests", logs.size(),
            "allowedRequests", allowed,
            "blockedRequests", blocked,
            "recentLogs", logs.stream().limit(10).toList()
        ));
    }

    // GET /api/admin/logs  — see all requests (no token needed for demo)
    @GetMapping("/admin/logs")
    public ResponseEntity<?> getAllLogs() {
        return ResponseEntity.ok(rateLimiterService.getAllLogs());
    }
}