package com.kiryukhin.mental_health.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.Instant;

@Data
@RedisHash("Token")
public class Token {
    @Id
    private String token;
    private String username;
    private Instant expiryDate;
    private boolean valid;
}
