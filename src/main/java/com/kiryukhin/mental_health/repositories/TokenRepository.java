package com.kiryukhin.mental_health.repositories;

import java.util.Optional;

import com.kiryukhin.mental_health.models.BaseEntity;
import com.kiryukhin.mental_health.models.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findTokenByTokenAndValidTrue(String token);

    Optional<Token> findTokenByTokenAndValidFalse(String token);

}
