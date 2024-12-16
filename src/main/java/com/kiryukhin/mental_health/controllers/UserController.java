package com.kiryukhin.mental_health.controllers;

import com.kiryukhin.mental_health.dtos.responses.UserResponseDto;
import com.kiryukhin.mental_health.servicesMapping.UserMappingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserMappingService userMappingService;

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        List<UserResponseDto> users = userMappingService.getUserList();
        return ResponseEntity.ok().body(users);
    }

}
