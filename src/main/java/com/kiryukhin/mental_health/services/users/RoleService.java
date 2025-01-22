package com.kiryukhin.mental_health.services.users;

import com.kiryukhin.mental_health.dtos.RoleDto;
import com.kiryukhin.mental_health.models.Role;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    List<RoleDto> getRoles();

    Optional<Role> getByRoleName(String rolename);

    Optional<RoleDto> getDtoByRoleName(String rolename);

    Optional<Role> getById(Long id);

    RoleDto createRole(RoleDto dto);

    RoleDto updateRole(RoleDto dto);
}
