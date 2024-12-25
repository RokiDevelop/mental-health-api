package com.kiryukhin.mental_health.models;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("VerificationToken")
public class VerificationToken {
    @Id
    private String token;

    private String email;

    private Instant expiryDate;

    private boolean isNotExpired;

    @Enumerated(EnumType.STRING)
    private TokenPurpose purpose;

    private boolean valid;
}
