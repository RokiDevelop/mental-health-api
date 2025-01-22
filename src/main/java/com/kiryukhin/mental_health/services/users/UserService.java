package com.kiryukhin.mental_health.services.users;

import com.kiryukhin.mental_health.dtos.UserCreateDto;
import com.kiryukhin.mental_health.dtos.UserUpdateDto;
import com.kiryukhin.mental_health.dtos.responses.UserResponseDto;
import com.kiryukhin.mental_health.models.User;

import java.util.List;
import java.util.Set;

public interface UserService {

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
