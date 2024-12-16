package com.kiryukhin.mental_health.servicesMapping;

import com.kiryukhin.mental_health.dtos.UserCreateDto;
import com.kiryukhin.mental_health.dtos.UserDto;
import com.kiryukhin.mental_health.dtos.UserUpdateDto;
import com.kiryukhin.mental_health.dtos.responses.UserResponseDto;
import com.kiryukhin.mental_health.mappers.UserMapper;
import com.kiryukhin.mental_health.models.User;
import com.kiryukhin.mental_health.servicesLogic.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
class UserMappingServiceImpl implements UserMappingService {

    private final UserMapper userMapper;
    private final UserService userService;

    @Override
    public UserResponseDto createUser(UserCreateDto dto) {
        User user = userService.createUser(dto);
        return userMapper.toUserResponseDto(user);
    }

    @Override
    public UserResponseDto createSuperuser(UserCreateDto dto) {
        User user = userService.createSuperuser(dto);
        return userMapper.toUserResponseDto(user);
    }

    @Override
    public List<UserResponseDto> getUserList() {
        List<User> users = userService.getUserList();
        return users.stream().map(userMapper::toUserResponseDto).toList();
    }

    @Override
    public UserResponseDto getById(Long id) {
        User user = userService.getById(id);
        return userMapper.toUserResponseDto(user);
    }

    @Override
    public UserResponseDto getByUsernameOrEmail(String usernameOrEmail) {
        User user = userService.getByUsernameOrEmail(usernameOrEmail);
        return userMapper.toUserResponseDto(user);
    }

    @Override
    public User getUserForUserDetails(String usernameOrEmail) {
        User user = userService.getByUsernameOrEmail(usernameOrEmail);
        return user;
    }

    @Override
    public UserResponseDto getByUsername(String username) {
        User user = userService.getByUsernameAndIsBlockedFalse(username);
        return userMapper.toUserResponseDto(user);
    }

    @Override
    public UserResponseDto getByEmail(String email) {
        User user = userService.getByEmailAndIsBlockedFalse(email);
        return userMapper.toUserResponseDto(user);
    }

    @Override
    public UserResponseDto updateUserByUsername(String username, UserUpdateDto dto) {
        UserDto userDto = userMapper.userUpdateDtoToUserDto(dto);
        User user = userService.updateUserByUsername(username, userDto);
        return userMapper.toUserResponseDto(user);
    }

    @Override
    public UserResponseDto updateUserByEmail(String email, UserUpdateDto dto) {
        UserDto userDto = userMapper.userUpdateDtoToUserDto(dto);
        User user = userService.updateUserByEmail(email, userDto);
        return userMapper.toUserResponseDto(user);
    }
}
