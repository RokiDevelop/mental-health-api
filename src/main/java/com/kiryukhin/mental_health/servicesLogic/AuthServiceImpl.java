package com.kiryukhin.mental_health.servicesLogic;

import com.kiryukhin.mental_health.dtos.RoleDto;
import com.kiryukhin.mental_health.dtos.UserCreateDto;
import com.kiryukhin.mental_health.dtos.requests.NewPasswordRequest;
import com.kiryukhin.mental_health.dtos.requests.RefreshTokenRequest;
import com.kiryukhin.mental_health.dtos.requests.RegistrationRequest;
import com.kiryukhin.mental_health.dtos.requests.TokenDto;
import com.kiryukhin.mental_health.dtos.responses.UserResponseDto;
import com.kiryukhin.mental_health.exeptions.RegistrationFailedException;
import com.kiryukhin.mental_health.mappers.RegistrationMapper;
import com.kiryukhin.mental_health.models.TokenPurpose;
import com.kiryukhin.mental_health.models.User;
import com.kiryukhin.mental_health.models.VerificationToken;
import com.kiryukhin.mental_health.repositories.UserRepository;
import com.kiryukhin.mental_health.utils.mail.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.ObjectNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
class AuthServiceImpl implements AuthService {
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final RoleService roleService;
    private final UserService userService;
    private final EmailService emailService;
    private final VerificationTokenService verificationTokenService;
    private final RegistrationMapper registrationMapper;
    private final UserRepository userRepository;

    @Override
    public UserResponseDto signUp(RegistrationRequest registrationRequest) {
        try {
            UserCreateDto userCreateDto = registrationMapper.toUserCreateDto(registrationRequest);
            RoleDto role =
                    roleService.getRoles().stream()
                            .filter(x -> x.getName().equals("ROLE_USER"))
                            .findFirst()
                            .get();

            userCreateDto.setRoles(Set.of(role));
            UserResponseDto userResponseDto = userService.createUser(userCreateDto);

            VerificationToken token = verificationTokenService.createToken(userResponseDto.getUsername(), TokenPurpose.ACCOUNT_ACTIVATION);
            emailService.sendRegistrationVerifier(userResponseDto.getEmail(), token.getToken());

            return userResponseDto;
        } catch (RegistrationFailedException e) {
            throw e;
        } catch (ObjectNotFoundException e) {
            throw new RegistrationFailedException("Registration failed. Role Not Found");
        }
    }

    @Override
    public TokenDto refreshToken(@Valid RefreshTokenRequest refreshTokenRequest) {
        return tokenService.generateTokenPairsViaRefreshToken(refreshTokenRequest.getRefreshToken());
    }

    @Override
    public void resetPassword(NewPasswordRequest newPasswordRequest) throws ObjectNotFoundException {
        VerificationToken token = verificationTokenService.validateToken(
                newPasswordRequest.getToken(), TokenPurpose.PASSWORD_RESET);

        User user = userRepository.getByEmail(token.getEmail());
        user.setPassword(passwordEncoder.encode(newPasswordRequest.getPassword()));
        userRepository.save(user);

        tokenService.invalidateAllTokensForUser(user.getUsername());
        verificationTokenService.invalidateToken(newPasswordRequest.getToken());
        emailService.sendUpdatedPassword(
                user.getEmail(), user.getEmail(), user.getUsername(), newPasswordRequest.getPassword());
    }

    @Override
    public void activateAccount(String token) {
        VerificationToken verificationToken = verificationTokenService.validateToken(
                token, TokenPurpose.ACCOUNT_ACTIVATION);
        String userEmail = verificationToken.getEmail();
        User user = userRepository.getByUsernameOrEmail(userEmail, userEmail);
        user.setVerified(true);
        userRepository.save(user);

        verificationTokenService.invalidateToken(token);
    }

    @Override
    public void confirmAction(String token) {
        VerificationToken verificationToken = verificationTokenService.validateToken(
                token, TokenPurpose.ACTION_CONFIRMATION);
//        TODO: add the implementation of the activation of the action that is required
        verificationTokenService.invalidateToken(token);
    }

    @Override
    public void preliminaryResetPassword(String emailOrUsername) {
        UserResponseDto user = userService.getByUsernameOrEmail(emailOrUsername);
        VerificationToken token = verificationTokenService.createToken(
                user.getEmail(), TokenPurpose.PASSWORD_RESET);
        emailService.sendResetPassword(user.getEmail(), token.getToken());
    }

    @Override
    public void preliminaryActivateAccount(String usernameOrEmail) {
        User user = userRepository.getByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
        if (user.isVerified()) {
            throw new RuntimeException("User already verified");
        }
        List<VerificationToken> oldVerificationToken = verificationTokenService.getTokensByEmail(user.getEmail());
        oldVerificationToken.forEach(verificationToken ->
                verificationTokenService.invalidateToken(verificationToken.getToken()));

        VerificationToken token = verificationTokenService.createToken(user.getEmail(), TokenPurpose.ACCOUNT_ACTIVATION);
        emailService.sendRegistrationVerifier(user.getEmail(), token.getToken());
    }

    @Override
    public void preliminaryConfirmAction(String usernameOrEmail) {
        User user = userRepository.getByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
        VerificationToken token = verificationTokenService.createToken(
                user.getEmail(), TokenPurpose.ACTION_CONFIRMATION);
        emailService.sendConfirmAction(user.getEmail(), token.getToken());
    }
}