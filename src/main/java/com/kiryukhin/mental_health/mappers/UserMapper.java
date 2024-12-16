package com.kiryukhin.mental_health.mappers;

import com.kiryukhin.mental_health.dtos.UserCreateDto;
import com.kiryukhin.mental_health.dtos.UserDto;
import com.kiryukhin.mental_health.dtos.UserUpdateDto;
import com.kiryukhin.mental_health.dtos.responses.UserResponseDto;
import com.kiryukhin.mental_health.models.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toUserDto(User entity);

    UserResponseDto toUserResponseDto(User entity);

    UserCreateDto toUserCreateDto(User entity);

    UserUpdateDto toUserUpdateDto(User entity);

    User userDtoToEntity(UserDto dto);

    User userCreateDtoToEntity(UserCreateDto dto);

    User userUpdateDtoToEntity(UserUpdateDto dto);

    UserDto userCreateDtoToUserDto(UserCreateDto dto);

    UserDto userUpdateDtoToUserDto(UserUpdateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePartialFromUserCreateDto(@MappingTarget User entity, UserCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePartialFromUserDto(@MappingTarget User entity, UserDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePartialFromUserUpdateDto(@MappingTarget User entity, UserUpdateDto dto);


}