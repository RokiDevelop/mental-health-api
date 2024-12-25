package com.kiryukhin.mental_health.servicesLogic;

import com.kiryukhin.mental_health.dtos.RoleDto;
import com.kiryukhin.mental_health.dtos.UserCreateDto;
import com.kiryukhin.mental_health.dtos.UserDto;
import com.kiryukhin.mental_health.dtos.requests.PasswordForgetRequest;
import com.kiryukhin.mental_health.dtos.requests.RefreshTokenRequest;
import com.kiryukhin.mental_health.dtos.requests.SetNewPasswordRequest;
import com.kiryukhin.mental_health.dtos.requests.TokenDto;
import com.kiryukhin.mental_health.dtos.responses.UserResponseDto;
import com.kiryukhin.mental_health.exeptions.RegistrationFailedException;
import com.kiryukhin.mental_health.mappers.UserMapper;
import com.kiryukhin.mental_health.models.User;
import com.kiryukhin.mental_health.utils.mail.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.ObjectNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    private final UserMapper userMapper;

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
            User user = userService.createUser(userCreateDto);
            emailService.sendRegistrationVerifier(user.getEmail(), "http://localhost:5173/confirm_email?code=dwef323t23432tf24hewmfrkngqerngerngenqeh34h34hrngekrqngerkh34hgnqelkrt3oijgporibkjneoknrgf");
            UserResponseDto userResponseDto = userService.createUser(userCreateDto);

            return user;

            return userResponseDto;
        } catch (RegistrationFailedException e) {
            throw e;
        } catch (ObjectNotFoundException e) {
            throw new RegistrationFailedException("Registration failed. Role Not Found");
        }
    }

    @Override
    public boolean resetPasswordRequest(PasswordForgetRequest passwordForgetRequest) {
        User user = userService.getByEmail(passwordForgetRequest.getEmail());

//        TODO: Generate url link with code
        emailService.sendResetPassword(user.getEmail(), "http://localhost:5173/confirm_email?code=dwef323t23432tf24hewmfrkngqerngerngenqeh34h34hrngekrqngerkh34hgnqelkrt3oijgporibkjneoknrgf");

        return true;
    }

    @Override
    public boolean setNewPassword(SetNewPasswordRequest setNewPasswordRequest) {
//        TODO: Validate code by username/email
        User user = userService.getByUsernameOrEmail(setNewPasswordRequest.getUsername());
        user.setPassword(passwordEncoder.encode(setNewPasswordRequest.getPassword()));

        UserDto userDto = userMapper.toUserDto(user);
        userService.updateUserByEmail(user.getEmail(), userDto);
        return true;
    }

    @Override
    public TokenDto refreshToken(@Valid RefreshTokenRequest refreshTokenRequest) {
        return tokenService.generateTokenPairsViaRefreshToken(refreshTokenRequest.getRefreshToken());
    }
}