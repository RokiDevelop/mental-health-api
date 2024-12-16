package com.kiryukhin.mental_health.mappers;

import com.kiryukhin.mental_health.dtos.UserCreateDto;
import com.kiryukhin.mental_health.dtos.requests.RegistrationRequest;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface RegistrationMapper {

    UserCreateDto toUserCreateDto(RegistrationRequest registrationRequest);

    RegistrationRequest toRegistrationRequest(UserCreateDto toDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePartial(@MappingTarget RegistrationRequest registrationRequest, UserCreateDto dto);
}
