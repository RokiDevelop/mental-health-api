package com.kiryukhin.mental_health.servicesLogic;


import com.kiryukhin.mental_health.dtos.RoleDto;
import com.kiryukhin.mental_health.mappers.RoleMapper;
import com.kiryukhin.mental_health.models.Role;
import com.kiryukhin.mental_health.repositories.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
class RoleServiceImpl implements RoleService {

    private final RoleRepository repository;
    private final RoleMapper mapper;

    @Override
    public List<RoleDto> getRoles() {
        return repository.findAll().stream().map(mapper::toDto).toList();
    }

    @Override
    public Optional<Role> getByRoleName(String rolename) {
        return repository.findRoleByNameIgnoreCase(rolename);
    }

    @Override
    public Optional<RoleDto> getDtoByRoleName(String rolename) {
        Optional<Role> role_opt = getByRoleName(rolename);
        return role_opt.map(mapper::toDto);
    }

    @Override
    public Optional<Role> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public RoleDto createRole(RoleDto dto) {
        Role role = repository.save(mapper.toEntity(dto));
        return mapper.toDto(role);
    }

    @Override
    public RoleDto updateRole(RoleDto dto) {
        Optional<Role> existing = getById(dto.getId());
        if (existing.isEmpty()) {
            throw new EntityNotFoundException();
        }
        mapper.updatePartial(existing.get(), dto);
        Role role = repository.save(existing.get());
        return mapper.toDto(role);
    }
}