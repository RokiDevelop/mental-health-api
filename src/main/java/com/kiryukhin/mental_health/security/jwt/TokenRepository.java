package com.kiryukhin.mental_health.security.jwt;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findTokenByTokenAndValidTrue(String token);
}
