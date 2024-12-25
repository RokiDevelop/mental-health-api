package com.kiryukhin.mental_health.servicesLogic;

import com.kiryukhin.mental_health.exeptions.TokenFailedException;
import com.kiryukhin.mental_health.models.TokenPurpose;
import com.kiryukhin.mental_health.models.VerificationToken;
import com.kiryukhin.mental_health.repositories.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationTokenService {

    private final VerificationTokenRepository tokenRepository;

    @Value("${token.verification.expiry.duration}")
    private long tokenExpiryDuration;

    public VerificationToken createToken(String email, TokenPurpose purpose) {
        VerificationToken token = new VerificationToken();
        token.setToken(UUID.randomUUID().toString());
        token.setEmail(email);
        token.setPurpose(purpose);
        if (purpose == TokenPurpose.ACCOUNT_ACTIVATION) {
            token.setNotExpired(true);
            token.setExpiryDate(Instant.now().plus(365, ChronoUnit.DAYS));
        } else {
            token.setNotExpired(false);
            token.setExpiryDate(Instant.now().plusSeconds(tokenExpiryDuration));
        }
        token.setValid(true);
        return tokenRepository.save(token);
    }

    public VerificationToken validateToken(String tokenValue, TokenPurpose purpose) {
        VerificationToken token = tokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new TokenFailedException("Verification token not found"));

        if (!token.isValid() || (token.getExpiryDate().isBefore(Instant.now()) && !token.isNotExpired())) {
            throw new TokenFailedException("Verification token is invalid or expired");
        }

        if (token.getPurpose() != purpose) {
            throw new TokenFailedException("Verification token purpose mismatch");
        }

        return token;
    }

    public void invalidateToken(String tokenValue) {
        VerificationToken token = tokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new TokenFailedException("Verification token not found"));

        token.setValid(false);
        tokenRepository.save(token);
    }

    public List<VerificationToken> getTokensByEmail(String email) {
        return tokenRepository.getAllByEmail(email);
    }
}
