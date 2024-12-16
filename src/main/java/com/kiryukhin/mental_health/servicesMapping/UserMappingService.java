package com.kiryukhin.mental_health.servicesMapping;

import com.kiryukhin.mental_health.dtos.UserCreateDto;
import com.kiryukhin.mental_health.dtos.UserUpdateDto;
import com.kiryukhin.mental_health.dtos.responses.UserResponseDto;
import com.kiryukhin.mental_health.models.User;

import java.util.List;

public interface UserMappingService {
    UserResponseDto createUser(UserCreateDto dto);

    UserResponseDto createSuperuser(UserCreateDto dto);

    List<UserResponseDto> getUserList();

    UserResponseDto getById(Long id);

    UserResponseDto getByUsernameOrEmail(String usernameOrEmail);

    UserResponseDto getByUsername(String username);

    UserResponseDto getByEmail(String email);

    UserResponseDto updateUserByUsername(String username, UserUpdateDto dto);

    UserResponseDto updateUserByEmail(String email, UserUpdateDto dto);


    User getUserForUserDetails(String usernameOrEmail);
}
