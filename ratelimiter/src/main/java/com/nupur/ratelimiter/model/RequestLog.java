package com.nupur.ratelimiter.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "request_logs")
public class RequestLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String apiToken;
    private String endpoint;
    private String status;   // "ALLOWED" or "BLOCKED"
    private LocalDateTime timestamp = LocalDateTime.now();
}