package com.kiryukhin.mental_health.servicesLogic;


import com.auth0.jwt.interfaces.DecodedJWT;
import com.kiryukhin.mental_health.dtos.requests.TokenDto;
import com.kiryukhin.mental_health.models.Token;
import jakarta.validation.constraints.NotBlank;

import java.util.Optional;

public interface TokenService {

    TokenDto generateTokenPairs(String username);

    TokenDto generateTokenPairsViaRefreshToken(String refreshTokenValue);

    String generateAccessToken(String username);

    DecodedJWT verifyJWT(String token);

    String generateRefreshToken(String username);

    void verifyRefreshToken(Token token);

    void invalidateRefreshToken(String jwt);

    Optional<Token> findInvalidatedTokenByValue(String token);

    void invalidateAccessToken(String jwt);

    void invalidateAllTokensForUser(@NotBlank String username);
}

