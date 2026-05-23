package com.nupur.ratelimiter.repository;

import com.nupur.ratelimiter.model.ApiUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ApiUserRepository extends JpaRepository<ApiUser, Long> {
    Optional<ApiUser> findByApiToken(String apiToken);
    Optional<ApiUser> findByUsername(String username);
}