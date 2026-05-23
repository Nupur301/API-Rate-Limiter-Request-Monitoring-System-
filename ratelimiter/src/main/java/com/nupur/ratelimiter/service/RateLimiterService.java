package com.nupur.ratelimiter.service;

import com.nupur.ratelimiter.model.ApiUser;
import com.nupur.ratelimiter.model.RequestLog;
import com.nupur.ratelimiter.repository.ApiUserRepository;
import com.nupur.ratelimiter.repository.RequestLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RateLimiterService {

    @Autowired
    private ApiUserRepository userRepo;

    @Autowired
    private RequestLogRepository logRepo;

    @Value("${rate.limit.window.minutes}")
    private int windowMinutes;

    // Register a new user and return their API token
    public String registerUser(String username) {
        if (userRepo.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        ApiUser user = new ApiUser();
        user.setUsername(username);
        user.setApiToken(UUID.randomUUID().toString()); // generates unique token
        userRepo.save(user);
        return user.getApiToken();
    }

    // Core method: check if request is allowed
    public boolean isAllowed(String token, String endpoint) {
        Optional<ApiUser> userOpt = userRepo.findByApiToken(token);

        if (userOpt.isEmpty()) {
            return false; // token doesn't exist
        }

        ApiUser user = userOpt.get();
        LocalDateTime windowStart = LocalDateTime.now().minusMinutes(windowMinutes);

        // Count how many requests this token made in the last window
        long recentRequests = logRepo.countByApiTokenAndTimestampAfter(token, windowStart);

        String status;
        boolean allowed;

        if (recentRequests >= user.getRequestLimit()) {
            status = "BLOCKED";
            allowed = false;
        } else {
            status = "ALLOWED";
            allowed = true;
        }

        // Always log the request
        RequestLog log = new RequestLog();
        log.setApiToken(token);
        log.setEndpoint(endpoint);
        log.setStatus(status);
        logRepo.save(log);

        return allowed;
    }

    // Get logs for a specific token
    public List<RequestLog> getLogsForToken(String token) {
        return logRepo.findByApiTokenOrderByTimestampDesc(token);
    }

    // Get all logs (admin)
    public List<RequestLog> getAllLogs() {
        return logRepo.findAllByOrderByTimestampDesc();
    }

    // Get user info by token
    public Optional<ApiUser> getUserByToken(String token) {
        return userRepo.findByApiToken(token);
    }
}