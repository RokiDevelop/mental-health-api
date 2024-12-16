package com.kiryukhin.mental_health.servicesLogic;

import com.kiryukhin.mental_health.dtos.UserCreateDto;
import com.kiryukhin.mental_health.dtos.UserDto;
import com.kiryukhin.mental_health.models.User;

import java.util.List;

public interface UserService {

    User createUser(UserCreateDto dto);

    User createSuperuser(UserCreateDto dto);

    List<User> getUserList();

    User getById(Long id);

    User getByUsernameOrEmail(String usernameOrEmail);

    User getByUsernameOrEmailAndIsBlockedFalse(String usernameOrEmail);

    User getByUsername(String username);

    User getByUsernameAndIsBlockedFalse(String username);

    User getByEmail(String email);

    User getByEmailAndIsBlockedFalse(String email);

    User updateUserByUsername(String username, UserDto dto);

    User updateUserByEmail(String email, UserDto userDto);
}
