package com.kiryukhin.mental_health.mappers;

import com.kiryukhin.mental_health.dtos.request.RegistrationRequest;
import com.kiryukhin.mental_health.dtos.UserDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface RegistrationMapper {

    UserDto toDto(RegistrationRequest registrationRequest);

    RegistrationRequest toRegistrationRequest(UserDto toDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePartial(@MappingTarget RegistrationRequest registrationRequest, UserDto dto);
}
