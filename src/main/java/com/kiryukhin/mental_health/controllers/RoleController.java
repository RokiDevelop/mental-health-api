package com.kiryukhin.mental_health.controllers;

import com.kiryukhin.mental_health.dtos.RoleDto;
import com.kiryukhin.mental_health.servicesLogic.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<?> getRoles() {
        List<RoleDto> roles = roleService.getRoles();
        return ResponseEntity.ok().body(roles);
    }

    @PostMapping
    public ResponseEntity<?> createRole(@RequestBody RoleDto userDto) {
        RoleDto role = roleService.createRole(userDto);
        return ResponseEntity.ok().body(role);
    }

    @PutMapping
    public ResponseEntity<?> updateRole(@RequestBody RoleDto userDto) {
        RoleDto role = roleService.updateRole(userDto);
        return ResponseEntity.ok().body(role);
    }
}