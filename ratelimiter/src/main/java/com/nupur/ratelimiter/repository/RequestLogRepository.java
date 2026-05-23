package com.nupur.ratelimiter.repository;

import com.nupur.ratelimiter.model.RequestLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface RequestLogRepository extends JpaRepository<RequestLog, Long> {

    // Count requests by a token within a time window
    long countByApiTokenAndTimestampAfter(String apiToken, LocalDateTime after);

    // Get all logs for a token (for dashboard)
    List<RequestLog> findByApiTokenOrderByTimestampDesc(String apiToken);

    // Get all logs (for admin dashboard)
    List<RequestLog> findAllByOrderByTimestampDesc();
}