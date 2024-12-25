package com.kiryukhin.mental_health.servicesLogic;

import com.kiryukhin.mental_health.dtos.UserCreateDto;
import com.kiryukhin.mental_health.dtos.UserUpdateDto;
import com.kiryukhin.mental_health.dtos.responses.UserResponseDto;
import com.kiryukhin.mental_health.models.User;

import java.util.List;

public interface UserService {

    User createUser(UserCreateDto dto);
    UserResponseDto createUser(UserCreateDto dto);

    UserResponseDto createSuperuser(UserCreateDto dto);

    List<UserResponseDto> getUserList();

    UserResponseDto getById(Long id);

    UserResponseDto getByUsernameOrEmail(String usernameOrEmail);

    UserResponseDto getByUsernameOrEmailAndIsBlockedFalse(String usernameOrEmail);

    UserResponseDto getByUsername(String username);

    UserResponseDto getByUsernameAndIsBlockedFalse(String username);

    UserResponseDto getByEmail(String email);

    UserResponseDto getByEmailAndIsBlockedFalse(String email);

    UserResponseDto updateUserByUsername(String username, UserUpdateDto dto);

    UserResponseDto updateUserByEmail(String email, UserUpdateDto userDto);

    User getUserForUserDetails(String usernameOrEmail);

    Set<String> findUsernameSetByUsername(String username);
}
