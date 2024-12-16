package com.kiryukhin.mental_health.mappers;

import com.kiryukhin.mental_health.dtos.RoleDto;
import com.kiryukhin.mental_health.models.Role;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    RoleDto toDto(Role role);

    Role toEntity(RoleDto role);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePartial(@MappingTarget Role entity, RoleDto dto);
}