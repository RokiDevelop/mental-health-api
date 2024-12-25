package com.kiryukhin.mental_health.servicesLogic;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.kiryukhin.mental_health.dtos.requests.TokenDto;
import com.kiryukhin.mental_health.dtos.responses.UserResponseDto;
import com.kiryukhin.mental_health.exeptions.TokenFailedException;
import com.kiryukhin.mental_health.models.Token;
import com.kiryukhin.mental_health.repositories.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
class TokenServiceImpl implements TokenService {
    private final TokenRepository tokenRepository;

    private final UserService userService;

    @Value("${secrets.jwt.KEY}")
    private String jwtKey;

    @Value("${secrets.jwt.ISSUER}")
    private String jwtIssuer;

    @Value("${secrets.jwt.EXPIRES_ACCESS_TOKEN_MINUTE}")
    private long accessTokenExpiryDuration;

    @Value("${secrets.jwt.EXPIRES_REFRESH_TOKEN_MINUTE}")
    private long refreshTokenExpiryDuration;

    @Override
    public TokenDto generateTokenPairs(String email) {
        UserResponseDto user = userService.getByUsernameOrEmail(email);
        return getTokenDto(user);
    }

    @Override
    public TokenDto generateTokenPairsViaRefreshToken(String refreshTokenValue) {
        var existingRefreshToken = tokenRepository.findTokenByToken(refreshTokenValue);
        if (existingRefreshToken.isEmpty()) {
            throw new TokenFailedException("refresh token not found!");
        }
        if (!existingRefreshToken.get().isValid()) {
            throw new TokenFailedException("refresh token not valid!");
        }
        verifyRefreshToken(existingRefreshToken.get());
        tokenRepository.delete(existingRefreshToken.get());
        UserResponseDto user = userService.getByUsername(existingRefreshToken.get().getUsername());
        return getTokenDto(user);
    }

    @Override
    public String generateAccessToken(String username) {
        Date expirationTime = new Date(
                System.currentTimeMillis()
                        + Duration.ofMinutes(accessTokenExpiryDuration).toMillis());

        Token access_token = new Token();
        access_token.setUsername(username);
        access_token.setValid(true);
        access_token.setExpiryDate(expirationTime.toInstant());
        access_token.setToken(JWT.create()
                .withSubject(username)
                .withExpiresAt(expirationTime)
                .withIssuer(jwtIssuer)
                .sign(Algorithm.HMAC256(jwtKey.getBytes())));
        access_token = tokenRepository.save(access_token);
        return access_token.getToken();
    }

    @Override
    public DecodedJWT verifyJWT(String token) {
        Algorithm algorithm = Algorithm.HMAC256(jwtKey.getBytes(StandardCharsets.UTF_8));
        JWTVerifier verifier = JWT.require(algorithm).build();
        try {
            return verifier.verify(token);
        } catch (JWTVerificationException e) {
            throw new TokenFailedException("Invalid JWT token: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public String generateRefreshToken(String username) {
        Instant expirationTime = Instant.now().plus(Duration.ofMinutes(refreshTokenExpiryDuration));
        Token refreshToken = new Token();
        refreshToken.setUsername(username);
        refreshToken.setValid(true);
        refreshToken.setExpiryDate(expirationTime);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken = tokenRepository.save(refreshToken);
        return refreshToken.getToken();
    }

    @Override
    public void verifyRefreshToken(Token token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            throw new TokenFailedException("Token has expired and cannot be used!");
        }
    }

    @Override
    public void invalidateRefreshToken(String jwt) {
        Optional<Token> existingToken = tokenRepository.findTokenByToken(jwt);

        if (existingToken.isPresent() && existingToken.get().isValid()) {
            throw new TokenFailedException("token already invalidated!");
        }

        Token token = new Token();
        token.setValid(false);
        token.setToken(jwt);
        tokenRepository.save(token);
    }

    @Override
    public Optional<Token> findInvalidatedTokenByValue(String token) {
        Optional<Token> optToken = tokenRepository.findTokenByToken(token);
        if (optToken.isPresent() && !optToken.get().isValid()) {
            return optToken;
        } else {
            return Optional.empty();
        }

    }

    @Override
    public void invalidateAccessToken(String jwt) {
        Optional<Token> existingToken = findInvalidatedTokenByValue(jwt);

        if (existingToken.isPresent()) {
            throw new TokenFailedException("token already invalidated!");
        }
        Token token = new Token();
        token.setUsername(JWT.decode(jwt).getSubject());
        token.setExpiryDate(JWT.decode(jwt).getExpiresAtAsInstant());
        token.setValid(false);
        token.setToken(jwt);
        tokenRepository.save(token);
    }


    private TokenDto getTokenDto(UserResponseDto user) {
        var accessToken = generateAccessToken(user.getUsername());
        var refreshToken = generateRefreshToken(user.getUsername());
        return TokenDto.builder()
                .refreshToken(refreshToken)
                .accessToken(accessToken)
                .user(user)
                .build();
    }
}

