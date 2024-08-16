package com.kiryukhin.mental_health.services;


import com.kiryukhin.mental_health.dtos.RoleDto;
import com.kiryukhin.mental_health.mappers.RoleMapper;
import com.kiryukhin.mental_health.models.Role;
import com.kiryukhin.mental_health.repositories.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository repository;
    private final RoleMapper mapper;

    public List<RoleDto> getRoles() {
        return repository.findAll().stream().map(mapper::toDto).toList();
    }

    public Optional<Role> getById(Long id) {
        return repository.findById(id);
    }

    public RoleDto createRole(RoleDto dto) {
        return mapper.toDto(repository.save(mapper.toEntity(dto)));
    }

    public RoleDto updateRole(RoleDto dto) {
        var existing = getById(dto.getId());
        if (existing.isEmpty()) {
            throw new EntityNotFoundException();
        }
        mapper.updatePartial(existing.get(), dto);
        return mapper.toDto(repository.save(existing.get()));
    }
}