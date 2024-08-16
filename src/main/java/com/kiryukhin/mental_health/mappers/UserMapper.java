package com.kiryukhin.mental_health.mappers;

import com.kiryukhin.mental_health.dtos.UserDto;
import com.kiryukhin.mental_health.models.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(User entity);

    User toEntity(UserDto entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePartial(@MappingTarget User entity, UserDto dto);
}