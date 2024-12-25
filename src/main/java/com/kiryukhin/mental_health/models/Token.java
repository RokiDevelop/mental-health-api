package com.kiryukhin.mental_health.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("Token")
public class Token {
    @Id
    private String token;

    @Indexed
    private String username;
    private Instant expiryDate;
    private boolean valid;
}
