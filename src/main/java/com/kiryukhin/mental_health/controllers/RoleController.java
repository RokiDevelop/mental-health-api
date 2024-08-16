package com.kiryukhin.mental_health.controllers;

import com.kiryukhin.mental_health.dtos.RoleDto;
import com.kiryukhin.mental_health.services.RoleService;
import java.util.List;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/role")
public class RoleController {

    private final com.kiryukhin.mental_health.services.RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public List<RoleDto> getRoles() {
        return roleService.getRoles();
    }

    @PostMapping
    public RoleDto createRole(@RequestBody RoleDto userDto) {
        return roleService.createRole(userDto);
    }
}