package com.kiryukhin.mental_health.repositories;

import com.kiryukhin.mental_health.models.VerificationToken;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface VerificationTokenRepository extends CrudRepository<VerificationToken, String> {
    Optional<VerificationToken> findByToken(String token);

    List<VerificationToken> getAllByEmail(String email);
}
