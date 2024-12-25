package com.kiryukhin.mental_health.repositories;

import com.kiryukhin.mental_health.models.Token;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends CrudRepository<Token, String> {
    Optional<Token> findTokenByToken(String token);

    List<Token> getAllByUsername(String username);

    @Override
    <S extends Token> S save(S entity);
}
