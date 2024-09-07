package com.kiryukhin.mental_health.services;

import com.kiryukhin.mental_health.dtos.*;
import com.kiryukhin.mental_health.dtos.request.*;
import com.kiryukhin.mental_health.mappers.RegistrationMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final TokenService tokenService;
    private final RoleService roleService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final RegistrationMapper mapper;
    private final PasswordEncoder passwordEncoder;

    public TokenDto login(LoginRequest loginRequest) {
        try {
            var u = new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword());
            log.debug(u.toString());

            authenticationManager.authenticate(u);
            return tokenService.generateTokenPairs(loginRequest.getUsername());
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new RuntimeException();
        }
    }

    public void logout(HttpServletRequest httpRequest) {
        String header = httpRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            throw new RuntimeException("logout is not successful!");
        }
        var jwt = header.substring(7);
        tokenService.invalidateToken(jwt);
    }

    public TokenDto signUp(HttpServletRequest httpRequest, RegistrationRequest registrationRequest) {
        try {
            RoleDto role =
                    roleService.getRoles().stream()
                            .filter(x -> x.getName().equals("USER"))
                            .findFirst()
                            .get();
            UserDto userData = mapper.toDto(registrationRequest);
            userData.setRoles(Set.of(role));
            userService.createUser(userData);

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            registrationRequest.getUsername(), registrationRequest.getPassword()));
            return tokenService.generateTokenPairs(userData.getUsername());
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public void resetPasswordRequest(PasswordForgetRequest passwordForgetRequest) {
        var user = userService.getDtoByEmail(passwordForgetRequest.getEmail());
//        TODO add email-service and send code for reset password
//        emailService
//        log.info(passwordResetUrl.formatted(emailService.getUsername(), emailService.getCode()));
    }

    public void setNewPassword(SetNewPasswordRequest setNewPasswordRequest) {
//        TODO: add email-service and validate code by username/email
        UserDto user;
        try {
            user = userService.getDtoByUsername(setNewPasswordRequest.getUsername());
        } catch (RuntimeException e) {
            user = userService.getDtoByEmail(setNewPasswordRequest.getUsername());
        }
        user.setPassword(passwordEncoder.encode(setNewPasswordRequest.getPassword()));
        userService.updateUser(user);
    }

    public TokenDto refreshToken(@Valid RefreshTokenRequest refreshTokenRequest) {
        return tokenService.generateTokenPairsViaRefreshToken(refreshTokenRequest.getRefreshToken());
    }
}