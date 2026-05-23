package com.nupur.ratelimiter.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "api_users")
public class ApiUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String apiToken;

    // Max requests allowed per minute for this user
    private int requestLimit = 10;

    private LocalDateTime createdAt = LocalDateTime.now();
}